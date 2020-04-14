package com.melvic.archia.interpreter

import com.melvic.archia.Query
import com.melvic.archia.leaf.Term
import com.melvic.archia.output.JsonArray
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json

typealias Evaluation = Result<JsonValue>
typealias KVEval = Result<Pair<String, JsonValue>>

fun interpret(query: Query): Evaluation {
    fun interpret(query: Query, parent: JsonValue): Evaluation {
        val output = when (query) {
            is Term -> interpretTerm(query)
            else -> Failed(UnknownQuery(query))
        }

        return when (output) {
            is Success<*> -> {
                val (key, value) = output.value()
                when (parent) {
                    is JsonObject -> Success(parent { key to value })
                    is JsonArray -> Success( parent { json { key to value }})
                    else -> throw Exception("Invalid Json")
                }
            }
            is Failed -> output
        }
    }

    return interpret(query, json {})
}

fun interpretTerm(term: Term): KVEval {
    val field = term.field ?: return Failed(missingField(term::field))

    val termFieldOut = json {
        "value" to str(field.value)
        field.boost?.let { "boost" to num(it) }
    }

    val termOut = Pair("term", json { "field" to termFieldOut })

    return Success(termOut)
}