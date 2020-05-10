package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class ExistsQuery : Clause() {
    var field: String by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::field)
}