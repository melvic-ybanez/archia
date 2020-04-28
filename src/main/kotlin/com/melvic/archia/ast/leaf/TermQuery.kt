package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithShortForm
import kotlin.reflect.KProperty

class TermQuery: WithShortForm<TermField, String>() {
    override fun initField(name: String) = TermField(name)

    override fun TermField.updateValue(value: String) {
        this.value = value
    }
}

class TermField(name: String) : Field(name) {
    var value: String by parameters
    var boost: Boost by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::value)
}

