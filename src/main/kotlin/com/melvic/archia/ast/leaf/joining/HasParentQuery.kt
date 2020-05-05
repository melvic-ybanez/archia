package com.melvic.archia.ast.leaf.joining

import com.melvic.archia.ast.WithQuery
import kotlin.reflect.KProperty

class HasParentQuery : WithQuery() {
    var parentType: String by parameters
    var score: Boolean by parameters
    var ignoreUnmapped by parameters

    override val extraRequiredParams: List<KProperty<Any>>
        get() = listOf(::parentType)
}