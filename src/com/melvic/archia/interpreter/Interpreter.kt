package com.melvic.archia.interpreter

import com.melvic.archia.Clause
import com.melvic.archia.Query
import com.melvic.archia.leaf.Term
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json

typealias Evaluation = Result<JsonValue>

fun interpret(query: Query): Evaluation {
    fun interpret(query: Clause, parent: JsonValue): Evaluation {
        val objectOrEmpty = if (parent is JsonObject) parent else json {}

        return when (query) {
            is Term -> interpretTerm(query, objectOrEmpty)
            else -> Success(json {})
        }
    }

    val output = query.queryClause?.let {
        interpret(it, json {})
    } ?: return Failed(missingField(query::query))

    return when (output) {
        is Failed -> output
        is Success<*> -> Success(json {
            "query" to output.value()
        })
    }
}

fun interpretTerm(term: Term, parent: JsonObject): Evaluation {
    val field = term.field ?: return Failed(missingField(term::field))

    val termFieldOut = json {
        "value" to str(field.value)
        field.boost?.let { "boost" to num(it) }
    }

    val termOut = parent { "term" to json { "field" to termFieldOut } }

    return Success(termOut)
}