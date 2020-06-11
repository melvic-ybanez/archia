package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class WildcardQuery : WithField<WildcardField>() {
    override fun initField(name: String): WildcardField {
        return WildcardField(name)
    }
}

class WildcardField(name: String) : Field(name) {
    var value: String by parameters
    var boost: Boost by parameters
    var rewrite: Rewrite by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::value)
}