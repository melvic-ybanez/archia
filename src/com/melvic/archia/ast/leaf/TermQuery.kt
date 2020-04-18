package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Init

data class TermQuery(var field: TermField? = null): Clause {
    operator fun String.invoke(init: TermField.() -> Unit) {
        field = TermField(this).apply(init)
    }
}

data class TermField(
    val name: String,
    var value: String? = null,
    var boost: Boost? = null
)

