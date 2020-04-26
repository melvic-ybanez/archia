package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithShortForm

class TermQuery: WithShortForm<TermField, String>() {
    override fun getField(name: String) = TermField(name)

    override fun TermField.updateValue(value: String) {
        this.value = value
    }
}

class TermField(
    name: String,
    var value: String? = null,
    var boost: Boost? = null
) : Field(name)

