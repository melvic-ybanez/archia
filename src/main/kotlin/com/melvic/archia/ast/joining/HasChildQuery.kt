package com.melvic.archia.ast.joining

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class HasChildQuery : WithQuery() {
    var type: String by parameters
    var maxChildren: Int by parameters
    var minChildren: Int by parameters
    var ignoreUnmapped: Boolean by parameters
    var scoreMode: ScoreMode by parameters

    override val extraRequiredParams: List<KProperty<Any>>
        get() = listOf(::type)
}