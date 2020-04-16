package com.melvic.archia.compound

import com.melvic.archia.Boost
import com.melvic.archia.MinimumShouldMatch
import com.melvic.archia.MultiClause

data class BoolQuery(
    var must: MultiClause? = null,
    var filter: MultiClause? = null,
    var should: MultiClause? = null,
    var mustNot: MultiClause? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var boost: Boost? = null
)