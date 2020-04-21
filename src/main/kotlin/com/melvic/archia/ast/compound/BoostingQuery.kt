package com.melvic.archia.ast.compound

import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.ClauseBuilder
import com.melvic.archia.ast.Init
import com.melvic.archia.ast.WithNum

data class BoostingQuery(
    var _positive: Clause? = null,
    var _negative: Clause? = null,
    var negativeBoost: Double? = null
): Clause, WithNum {
    private var builder: ClauseBuilder = ClauseBuilder()

    fun positive(init: Init<ClauseBuilder>) {
        _positive = builder.apply(init).clause
    }

    fun negative(init: Init<ClauseBuilder>) {
        _negative = builder.apply(init).clause
    }
}