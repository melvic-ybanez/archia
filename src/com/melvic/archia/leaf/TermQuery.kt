package com.melvic.archia.leaf

import com.melvic.archia.Boost
import com.melvic.archia.Clause

data class TermQuery(var field: TermField? = null): Clause() {
    operator fun String.invoke(init: TermField.() -> Unit) {
        field = TermField(this).apply(init)
    }
}

data class TermField(
    val name: String,
    var value: String? = null,
    var boost: Boost? = null
)
