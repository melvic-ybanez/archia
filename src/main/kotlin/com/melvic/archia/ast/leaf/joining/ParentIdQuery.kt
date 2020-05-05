package com.melvic.archia.ast.leaf.joining

import com.melvic.archia.ast.Clause
import kotlin.reflect.KProperty

class ParentIdQuery : Clause() {
    var type: String by parameters
    var id: String by parameters
    var ignoreUnmapped: Boolean by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::type, ::id)
}