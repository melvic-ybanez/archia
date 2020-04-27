package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.output.*
import com.melvic.archia.validate

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
