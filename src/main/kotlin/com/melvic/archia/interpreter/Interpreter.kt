package com.melvic.archia.interpreter

import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Init
import com.melvic.archia.ast.Query
import com.melvic.archia.ast.buildQuery
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.*
import com.melvic.archia.ast.leaf.RangeQuery
import com.melvic.archia.ast.leaf.TermQuery
import com.melvic.archia.interpreter.fulltext.interpret
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import com.melvic.archia.script.Script

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

        // Compound clauses
        is BoolQuery -> interpret(parentObject)
        is BoostingQuery -> interpret(parentObject)
        is ConstantScoreQuery -> interpret(parentObject)
        is DisMaxQuery -> interpret(parentObject)
        is FunctionScoreQuery -> interpret(parentObject)

        // Full text
        is IntervalsQuery -> interpret(parentObject)
        is MatchQuery -> interpret(parentObject)
        is MatchBoolPrefixQuery -> interpret(parentObject)
        is MatchPhraseQuery -> interpret(parentObject)
        is MatchPhrasePrefixQuery -> interpret(parentObject)
        is MultiMatchQuery -> interpret(parentObject)
        is CommonTermsQuery -> interpret(parentObject)

        // Scripts
        is Script -> interpret()

        else -> json {}.success()
    }
}