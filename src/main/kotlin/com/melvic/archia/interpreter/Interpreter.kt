package com.melvic.archia.interpreter

import QueryStringQuery
import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.*
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

fun Clause.interpret(parent: JsonObject = json {}): Evaluation {
    return when (this) {
        // Full text
        //is MatchBoolPrefixQuery -> interpret(parent)
        //is MatchPhraseQuery -> interpret(parent)
        //is MatchPhrasePrefixQuery -> interpret(parent)
        //is MultiMatchQuery -> interpret(parent)
        //is QueryStringQuery -> interpret(parent)

        is WithShortForm<*, *> -> this.interpret(parent)

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
            // A parameter with a Param value has a customized parameter name, specified
            // as the first element of the Param pair
            val (paramName, paramValue) = if (value is Pair<*, *> && value.first is String) {
                Pair((value.first as String).toSnakeCase(), value.second)
            } else Pair(fieldName.toSnakeCase(), value)

            // If the parameter is a field, the field's name becomes the key in the JSON object
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
        is MultiMatchType -> value.json().success()

        // Elasticsearch params
        is MinimumShouldMatch -> value.interpret().validate()
        is Fuzziness -> value.interpret().validate()
        is Geo -> value.interpret().validate()

        // If it's a list, recursively evaluate each item
        is List<*> -> {
            val errors = mutableListOf<ErrorCode>()
            val arrayOut = jsonArray()

            for (item in value) {
                var isPair = false
                val (paramName, paramValue) = if (item is Pair<*, *> && item.first is String) {
                    isPair = true
                    Pair((item.first as String).toSnakeCase(), item.second)
                } else Pair(name, item)

                when (val eval = interpretParam(paramName, paramValue)) {
                    is Failed -> errors.addAll(eval.errors)
                    is Success<*> -> arrayOut.add(run {
                        if (isPair) json { paramName to eval.value() }
                        else eval.value()
                    })
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

        else -> {
            println(value)
            InvalidValue(name, value).fail()
        }
    }
}

fun <F : Field, V> WithShortForm<F, V>.interpret(parent: JsonObject): Evaluation {
    return json {
        validateRequiredParams(this@interpret)

        esName() to run {
            customProp?.let {
                val (paramName, paramValue) = it
                json { paramName to interpretParam(paramName, paramValue) }
            } ?: interpretParamList(parameters, parent)
        }
    }.validate()
}