package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Clause
import kotlin.reflect.KProperty

class IdsQuery : Clause() {
    var values: List<String> by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::values)
}