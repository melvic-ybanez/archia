package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

data class BoolQuery(
    var _must: MultiClause? = null,
    var _filter: MultiClause? = null,
    var _should: MultiClause? = null,
    var _mustNot: MultiClause? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var boost: Boost? = null
): Clause(), WithNum, BuilderHelper {
    fun must(init: Init<ClauseArrayBuilder>) {
        setClauseArray(init) { _must = it }
    }

    fun filter(init: Init<ClauseArrayBuilder>) {
        setClauseArray(init) { _filter = it }
    }

    fun should(init: Init<ClauseArrayBuilder>) {
        setClauseArray(init) { _should = it }
    }

    fun mustNot(init: Init<ClauseArrayBuilder>) {
        setClauseArray(init) { _mustNot = it }
    }
}