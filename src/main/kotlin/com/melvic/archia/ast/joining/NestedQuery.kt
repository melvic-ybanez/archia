package com.melvic.archia.ast.joining

import com.melvic.archia.ast.BuilderHelper
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.ClauseBuilder
import com.melvic.archia.ast.Init
import kotlin.reflect.KProperty

class NestedQuery : Clause(), BuilderHelper {
    var path: String by parameters
    var query: Clause by parameters
    var scoreMode: ScoreMode by parameters
    var ignoreUnmapped: Boolean by parameters

    fun query(init: Init<ClauseBuilder>) {
        setClause(init) { query = it }
    }

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::path, ::query)
}

enum class ScoreMode {
    AVG, MAX, MIN, NONE, SUM
}