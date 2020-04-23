package com.melvic.archia.interpreter

import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.json

/**
 * Interprets an intervals query
 * @param parent the json object that will contain the interpreted
 * form of the given intervals query
 */
fun IntervalsQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(::field)

    val fieldOut = with(field) {
        json {

        }
    }

    val intervalOut = parent { esName() to json { field.name to fieldOut } }
    return intervalOut.validate()
}