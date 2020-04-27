package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class BoostingQuery: Clause(), WithNum, BuilderHelper {
    var positive: Clause by parameters
    var negative: Clause by parameters
    var negativeBoost: Double by parameters

    fun positive(init: Init<ClauseBuilder>) {
        setClause(init) { positive = it }
    }

    fun negative(init: Init<ClauseBuilder>) {
       setClause(init) { negative = it }
    }

    override val requiredParams: List<KProperty<Any>> =
        listOf(::positive, ::negative)
}