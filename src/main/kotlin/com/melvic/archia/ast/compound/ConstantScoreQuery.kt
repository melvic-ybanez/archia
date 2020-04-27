package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class ConstantScoreQuery: Clause(), BuilderHelper {
    var filter: Clause by parameters
    var boost: Boost by parameters

    fun filter(init: Init<ClauseBuilder>) {
        setClause(init) { filter = it }
    }

    override val requiredParams: List<KProperty<Any>> = listOf(::filter)
}