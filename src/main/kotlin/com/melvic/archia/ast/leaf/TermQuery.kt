package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField

class TermQuery: WithField<TermField>() {
    var customProp: Pair<String, String>? = null

    operator fun String.invoke(init: TermField.() -> Unit) {
        field = TermField(this).apply(init)
    }

    infix fun String.to(_value: String) {
        customProp = Pair(this, _value)
        this { value = _value }
    }
}

class TermField(
    name: String,
    var value: String? = null,
    var boost: Boost? = null
) : Field(name)

