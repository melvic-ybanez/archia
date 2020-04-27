package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class DisMaxQuery: Clause(), BuilderHelper {
    var queries: MultiClause by parameters
    var tieBreaker: Double by parameters

    fun queries(init: Init<ClauseArrayBuilder>) {
        setClauseArray(init) { queries = it }
    }

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::queries)
}