package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.output.*
import com.melvic.archia.validate

fun BoolQuery.interpret(parent: JsonObject): Evaluation {
    val propsOut = json {
        propFunc(::_must) { it.interpret() }
        propFunc(::_should) { it.interpret() }
        propFunc(::_filter) { it.interpret() }
        propFunc(::_mustNot) { it.interpret() }
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
        propFunc(::_positive) { it.interpret() }
        propFunc(::_negative) { it.interpret() }
        prop(::negativeBoost) { it.json() }
    }

    val boostingOut = parent { esName() to propsOut }
    return boostingOut.success()
}

fun ConstantScoreQuery.interpret(parent: JsonObject): Evaluation {
    if (_filter == null) return missingField(::filter)

    return parent {
        esName() to json {
            propFunc(::_filter) { it.interpret() }
            prop(::boost) { it.json() }
        }
    }.success()
}

fun DisMaxQuery.interpret(parent: JsonObject): Evaluation {
    if (_queries == null) return missingField(::queries)

    return parent {
        esName() to json {
            propWithAlt(::_queries, ::queries) { it.interpret() }
            prop(::tieBreaker) { it.json() }
        }
    }.success()
}

fun FunctionScoreQuery.interpret(parent: JsonObject): Evaluation {
    val propsOut = json {
        propWithAlt(::_query, ::query) { it.interpret() }
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
        propNum(::maxBoost, ::minScore)
        propEnum(::scoreMode)
        propEnum(::boostMode)
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

fun FunctionClause.interpretFunction(parent: JsonObject): Evaluation {
    return parent {
        propWithAlt(::_filter, ::filter) { it.interpret() }
        propNum(::weight)
        propParam(scoreFunction) { it.interpret() }

    }.success()
}

fun ScoreFunction.interpret(): Evaluation {
    return when (this) {
        is ScriptScore -> interpret()
        is RandomScore -> interpret()
        is FieldValueFactor -> interpret()
        is DecayFunction -> interpret()
        else -> JsonNull.success()
    }
}

fun ScriptScore.interpret(): Evaluation {
    return json {
        propWithAlt(::_script, ::script) { it.interpret() }
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
                is ANumber ->
                    // numeric for origin are still represented as strings
                    fieldType.value.toString().json()
                is ADate  -> fieldType.value.toString().json()
                else -> JsonNull
            } }
            propStr(::scale, ::offset)
            propNum(::decay)
        }
    }
    val decayOut = json {
        field.name to decayFieldOut
        propEnum(::multiValueMode)
    }
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
