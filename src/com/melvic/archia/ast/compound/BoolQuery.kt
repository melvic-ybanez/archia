package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.ClauseArrayBuilder

data class BoolQuery(
    var _must: MultiClause? = null,
    var _filter: MultiClause? = null,
    var _should: MultiClause? = null,
    var _mustNot: MultiClause? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var boost: Boost? = null
): Clause {
    private fun setArrayClause(init: Init<ClauseArrayBuilder>, set: (MultiClause) -> Unit) {
        val builder = ClauseArrayBuilder().apply(init)
        set(builder.clauses)
    }

    fun must(init: Init<ClauseArrayBuilder>) {
        setArrayClause(init) { _must = it }
    }

    fun filter(init: Init<ClauseArrayBuilder>) {
        setArrayClause(init) { _filter = it }
    }

    fun should(init: Init<ClauseArrayBuilder>) {
        setArrayClause(init) { _should = it }
    }

    fun mustNot(init: Init<ClauseArrayBuilder>) {
        setArrayClause(init) { _mustNot = it }
    }
}