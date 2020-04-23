package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

data class ConstantScoreQuery(
    var _filter: Clause? = null,
    var boost: Boost? = null
): Clause, BuilderHelper {
    fun filter(init: Init<ClauseBuilder>) {
        setClause(init) { _filter = it }
    }
}