package com.melvic.archia.interpreter.fulltext

import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.ast.fulltext.MatchRule
import com.melvic.archia.interpreter.*
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
            // TODO implement interpreter
        }
    }
}

fun MatchRule.interpret(): Evaluation {
    if (query == null) return missingField(::query)

    return json {
        propStr(::query)
        propNum(::maxGaps)
        propBool(::ordered)
        propStr(::analyzer)
        // TODO: filter rule
        propStr(::useField)
    }.success()
}