package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

data class BoostingQuery(
    var _positive: Clause? = null,
    var _negative: Clause? = null,
    var negativeBoost: Double? = null
): Clause(), WithNum, BuilderHelper {
    fun positive(init: Init<ClauseBuilder>) {
        setClause(init) { _positive = it }
    }

    fun negative(init: Init<ClauseBuilder>) {
       setClause(init) { _negative = it }
    }
}