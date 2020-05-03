package com.melvic.archia.ast.joining

import com.melvic.archia.ast.Clause
import kotlin.reflect.KProperty

class NestedQuery : Clause() {
    var path: String by parameters
    var query: Clause by parameters
    var scoreMode: ScoreMode by parameters
    var ignoreUnmapped: Boolean by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::path, ::query)
}

enum class ScoreMode {
    AVG, MAX, MIN, NONE, SUM
}