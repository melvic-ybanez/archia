package com.melvic.archia.ast.compound

import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.ClauseBuilder
import com.melvic.archia.ast.Init
import com.melvic.archia.ast.WithNum

data class BoostingQuery(
    var _positive: Clause? = null,
    var _negative: Clause? = null,
    var negativeBoost: Float? = null
): Clause, WithNum {
    fun positive(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        _positive = builder.clause
    }

    fun negative(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        _negative = builder.clause
    }
}