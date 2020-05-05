package com.melvic.archia.ast.leaf.joining

import com.melvic.archia.ast.WithQuery
import kotlin.reflect.KProperty

class NestedQuery : WithQuery() {
    var path: String by parameters
    var scoreMode: ScoreMode by parameters
    var ignoreUnmapped: Boolean by parameters

    override val extraRequiredParams: List<KProperty<Any>>
        get() = listOf(::path)
}