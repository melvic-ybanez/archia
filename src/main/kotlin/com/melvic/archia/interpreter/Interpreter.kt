package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.output.*
import kotlin.reflect.KCallable

typealias Evaluation = Result<JsonValue>

fun interpret(init: Init<Query>) = buildQuery(init).interpret()

fun Query.interpret(): Evaluation {
    val output = this.queryClause?.interpret() ?: missingField(this::query)

    return when (output) {
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
        is MatchQuery -> interpret(parentObject)
        is MatchAllQuery -> interpret(parentObject)
        is MatchNoneQuery -> interpret(parentObject)
        is RangeQuery -> interpret(parentObject)

        // Compound clauses
        is BoolQuery -> interpret(parentObject)
        is BoostingQuery -> interpret(parentObject)
        is ConstantScoreQuery -> interpret(parentObject)
        is DisMaxQuery -> interpret(parentObject)
        is FunctionScoreQuery -> interpret(parentObject)

        else -> json {}.success()
    }
}