package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

data class ConstantScoreQuery(
    var _filter: Clause? = null,
    var boost: Boost? = null
): Clause, WithNum {
    private var builder: ClauseBuilder = ClauseBuilder()

    fun filter(init: Init<ClauseBuilder>) {
        _filter = builder.apply(init).clause
    }
}