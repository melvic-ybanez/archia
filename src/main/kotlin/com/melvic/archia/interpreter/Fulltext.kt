package com.melvic.archia.interpreter

import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.json

fun IntervalsQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(::field)

    val fieldOut = with(field) {
        json {
            // TODO: implement interpreter for the intervals query
        }
    }

    val intervalOut = parent { esName() to json { field.name to fieldOut } }
    return intervalOut.validate()
}