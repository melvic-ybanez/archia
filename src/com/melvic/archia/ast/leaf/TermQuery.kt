package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Init

data class TermQuery(var field: TermField? = null): Clause {
    private var customProp: Pair<String, String>? = null

    operator fun String.invoke(init: TermField.() -> Unit) {
        field = TermField(this).apply(init)
    }

    infix fun String.to(value: String) {
        customProp = Pair(this, value)
    }
}

data class TermField(
    val name: String,
    var value: String? = null,
    var boost: Boost? = null
)

