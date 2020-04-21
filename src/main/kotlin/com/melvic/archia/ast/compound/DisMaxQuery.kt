package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

data class DisMaxQuery(
    var _queries: MultiClause? = null,
    var tieBreaker: Double? = null
): Clause {
    fun queries(init: Init<ClauseArrayBuilder>) {
        _queries = ClauseArrayBuilder().apply(init).clauses
    }
}