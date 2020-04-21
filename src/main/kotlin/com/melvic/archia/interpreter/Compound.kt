package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.output.*

fun BoolQuery.interpret(parent: JsonObject): Evaluation {
    val propsOut = json {
        propFunc(::_must, ::must) { it.interpret() }
        propFunc(::_should, ::should) { it.interpret() }
        propFunc(::_filter, ::filter) { it.interpret() }
        propFunc(::_mustNot, ::mustNot) { it.interpret() }
        prop(::minimumShouldMatch) { it.interpret(this) }
        prop(::boost) { it.json() }
    }
    val boolOut = parent { esName() to propsOut }
    return boolOut.validate()
}

fun BoostingQuery.interpret(parent: JsonObject): Evaluation {
    val errors = mutableListOf<ErrorCode>()

    if (_positive == null) errors.add(missingFieldCode(::positive))
    if (_negative == null) errors.add(missingFieldCode(::negative))

    if (errors.isNotEmpty()) return Failed(errors)

    val propsOut = json {
        propFunc(::_positive, ::positive) { it.interpret() }
        propFunc(::_negative, ::negative) { it.interpret() }
        prop(::negativeBoost) { it.json() }
    }

    val boostingOut = parent { esName() to propsOut }
    return boostingOut.success()
}

fun ConstantScoreQuery.interpret(parent: JsonObject): Evaluation {
    if (_filter == null) return missingField(::filter)

    return parent {
        esName() to json {
            propFunc(::_filter, ::filter) { it.interpret() }
            prop(::boost) { it.json() }
        }
    }.success()
}

fun DisMaxQuery.interpret(parent: JsonObject): Evaluation {
    if (_queries == null) return missingField(::queries)

    return parent {
        esName() to json {
            propFunc(::_queries, ::queries) { it.interpret() }
            prop(::tieBreaker) { it.json() }
        }
    }.success()
}

fun FunctionScoreQuery.interpret(parent: JsonObject): Evaluation {
    val propsOut = json {
        propFunc(::_query, ::query) { it.interpret() }
        propStr(::boost)
        _functions?.let {
            val functions = array()
            for (function in it) {
                when (val result = function.interpretFunction(json {})) {
                    is Failed -> errors.addAll(result.errors)
                    is Success<*> -> functions.add(result.value())
                }
            }
            "functions" to functions
        }
        propNum(::maxBoost)
        propEnum(::scoreMode)
        propEnum(::boostMode)
        propNum(::minScore)
    }

    val functionScoreOut = this.interpretFunction(propsOut)

    return parent { esName() to functionScoreOut }.validate()
}

fun MultiClause.interpret(): Evaluation {
    if (this.size == 1)
        return this[0].interpret()

    val result: JsonArray = jsonArray()

    for (clause in this) {
        val eval = clause.interpret()
        if (eval is Failed) return eval
        result.add(eval.value())
    }

    return result.success()
}

fun MinimumShouldMatch.interpret(parent: JsonObject): JsonValue {
    val msm: MinimumShouldMatch = this
    return with(parent) {
        fun interpretSimple(it: SimpleMSM): JsonValue = when (it) {
            is ANumber -> it.value.json()
            is Percent -> "${it.value}%".json()
            else -> JsonNull
        }
        fun interpretMin(it: MinimumShouldMatch): JsonValue = when (it) {
            is SimpleMSM -> interpretSimple(it)
            is Combination -> "${it.value}<${interpretSimple(it.simple)}".json()
            is Multiple -> array(it.values.map { i -> interpretMin(i) })
            else -> JsonNull
        }
        interpretMin(msm)
    }
}

fun FunctionClause.interpretFunction(parent: JsonObject): Evaluation {
    return parent {
        propFunc(::_filter, ::filter) { it.interpret() }
        propFunc(::_scriptScore, ::scriptScore) { it.interpret() }
        propNum(::weight)
        propFunc(::_randomScore, ::randomScore) { it.interpret() }
        propFunc(::_fieldValueFactor, ::fieldValueFactor) { it.interpret() }

        // decay functions
        propFunc(::_gauss, ::gauss) { it.interpret() }
        propFunc(::_exp, ::exp) { it.interpret() }
        propFunc(::_linear, ::linear) { it.interpret() }
    }.success()
}

fun ScriptScore.interpret(): Evaluation {
    return json {
        propFunc(::_script, ::script) { it.interpret() }
    }.success()
}

fun RandomScore.interpret(): Evaluation {
    return json {
        propNum(::seed)
        propStr(::field)
    }.success()
}

fun FieldValueFactor.interpret(): Evaluation {
    return json {
        propStr(::field)
        propNum(::factor)
        propEnum(::modifier)
        propNum(::missing)
    }.success()
}

fun DecayFunction.interpret(): Evaluation {
    val field = this.field ?: return missingField(this::field)

    val decayFieldOut = json {
        with(field) {
            prop(::origin) { fieldType -> when (fieldType) {
                is Geo -> fieldType.interpret()
                is ANumber -> fieldType.value.json()
                is ADate  -> fieldType.value.toString().json()
                else -> JsonNull
            } }
            propStr(::scale)
            propStr(::offset)
            propNum(::decay)
        }
    }
    val decayOut = json { field.name to decayFieldOut }
    return decayOut.success()
}

fun Geo.interpret(): JsonValue {
    return when (this) {
        is GeoObject -> with(this) {
            json { propNum(::lat); propNum(::long) }
        }
        is GeoString -> JsonString("${this.lat},${this.long}")
        is GeoHash -> JsonString(this.hash)
        is GeoArray -> jsonArray(JsonNumber(lat), JsonNumber(long))
        is GeoWktPoint -> JsonString("POINT (${this.lat} )")
    }
}
