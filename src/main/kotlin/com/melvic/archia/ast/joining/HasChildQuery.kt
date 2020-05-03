package com.melvic.archia.ast.joining

import com.melvic.archia.ast.BuilderHelper
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.ClauseBuilder
import com.melvic.archia.ast.Init
import kotlin.reflect.KProperty

class HasChildQuery : Clause(), BuilderHelper {
    var type: String by parameters
    var query: Clause by parameters
    var maxChildren: Int by parameters
    var minChildren: Int by parameters
    var ignoreUnmapped: Boolean by parameters
    var scoreMode: ScoreMode by parameters

    fun query(init: Init<ClauseBuilder>) {
        setClause(init) { query = it }
    }

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::type, ::query)
}