package com.melvic.archia.interpreter.fulltext

import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.ast.fulltext.MatchRule
import com.melvic.archia.interpreter.Evaluation
import com.melvic.archia.interpreter.interpret
import com.melvic.archia.interpreter.propFunc
import com.melvic.archia.interpreter.propStr
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json

/**
 * Interprets an intervals query
 * @param parent the json object that will contain the interpreted
 * form of the given intervals query
 */
fun IntervalsQuery.interpret(parent: JsonObject): Evaluation {
    return interpret(parent) {
        json {

        }
    }
}

fun MatchRule.interpret(): JsonValue {
    return json {
        propStr(::query)
    }
}