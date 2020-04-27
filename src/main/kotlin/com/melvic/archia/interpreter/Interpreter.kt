package com.melvic.archia.interpreter

import QueryStringQuery
import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.*
import com.melvic.archia.ast.leaf.RangeQuery
import com.melvic.archia.ast.leaf.TermQuery
import com.melvic.archia.interpreter.fulltext.interpret
import com.melvic.archia.output.*
import com.melvic.archia.script.Script
import com.melvic.archia.validate
import com.melvic.archia.validateRequiredParams

typealias Evaluation = Result<JsonValue>

fun interpret(init: Init<Query>) = buildQuery(init).interpret()

fun Query.interpret(): Evaluation {
    return when (val output = this.clause?.interpret() ?: missingField(this::query)) {
        is Failed -> output
        is Success<*> -> json {
            "query" to output.value()
        }.success()
    }
}

fun Clause.interpret(parent: JsonValue = json {}): Evaluation {
    val parentObject = if (parent is JsonObject) parent else json {}

    return when (this) {
        // Leaf clauses
        is TermQuery -> interpret(parentObject)
        is MatchAllQuery -> interpret(parentObject)
        is MatchNoneQuery -> interpret(parentObject)
        is RangeQuery -> interpret(parentObject)

        // Full text
        is IntervalsQuery -> interpret(parentObject)
        is MatchQuery -> interpret(parentObject)
        is MatchBoolPrefixQuery -> interpret(parentObject)
        is MatchPhraseQuery -> interpret(parentObject)
        is MatchPhrasePrefixQuery -> interpret(parentObject)
        is MultiMatchQuery -> interpret(parentObject)
        is CommonTermsQuery -> interpret(parentObject)
        is QueryStringQuery -> interpret(parentObject)

        // Scripts
        is Script -> interpret()

        else -> json {
            validateRequiredParams(this@interpret)
            esName() to interpretParamList(parameters, json {})
        }.validate()
    }
}

fun TreeNode.interpretSubTree(): Evaluation {
    return json {
        validateRequiredParams(this@interpretSubTree)
        interpretParamList(parameters, this)
    }.validate()
}

fun interpretParamList(parameters: Map<String, Any?>, parent: JsonObject): JsonValue {
    return parent {
        for ((fieldName, value) in parameters) {
            val (paramName, paramValue) = if (value is Pair<*, *> && value.first is String) {
                Pair((value.first as String).toSnakeCase(), value.second)
            } else Pair(fieldName.toSnakeCase(), value)

            val fieldParamName = if (paramValue is Field) paramValue.name else paramName

            fieldParamName to interpretParam(fieldName, paramValue)
        }
    }
}

fun <V> interpretParam(name: String, value: V): Evaluation {
    return when (value) {
        // Primitive values
        is Number -> value.json().success()
        is Boolean -> value.json().success()
        is String -> value.json().success()
        is ADate  -> value.value.toString().json().success()

        is AString -> value.value.json().success()

        // Enums
        is Operator -> value.json().success()
        is BoostMode -> value.json().success()
        is ScoreMode -> value.json().success()
        is Modifier -> value.json().success()
        is MultiValueMode -> value.json().success()

        // Elasticsearch params
        is MinimumShouldMatch -> value.interpret().validate()
        is Fuzziness -> value.interpret().validate()
        is Geo -> value.interpret().validate()

        // If it's a list, recursively evaluate each item
        is List<*> -> {
            val errors = mutableListOf<ErrorCode>()
            val arrayOut = jsonArray()

            for (item in value) {
                when (val eval = interpretParam(name, item)) {
                    is Failed -> errors.addAll(eval.errors)
                    is Success<*> -> arrayOut.add(eval.value())
                }
            }

            if (errors.isEmpty()) arrayOut.success()
            else Failed(errors)
        }

        is OneOrMore<*> ->  when (val result = interpretParam(name, value.items)) {
            is Failed -> result
            is Success<*> -> {
                val arrayOut = result.value() as JsonArray
                if (arrayOut.items.size == 1) {
                    arrayOut.items[0].success()
                } else arrayOut.success()
            }
        }

        // interpret child clause
        is Clause -> if (value.topLevel) value.interpret() else value.interpretSubTree()
        is TreeNode -> value.interpretSubTree()

        else -> InvalidValue(name, value).fail()
    }
}