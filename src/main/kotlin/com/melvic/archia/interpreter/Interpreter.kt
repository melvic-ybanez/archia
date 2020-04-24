package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.output.*
import com.melvic.archia.validate

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

/**
 * Interprets a clause with a field, if the field exists. If the field is null,
 * the function will yield a missing field error.
 * @param build builder for the field. It only gets executed if the field exists.
 */
fun <F : Field> WithField<F>.interpret(parent: JsonObject, build: F.() -> JsonValue): Evaluation {
    val field = this.field ?: return missingField(this::field)
    val out = parent { esName() to json { field.name to build(field) }}
    return out.validate()
}