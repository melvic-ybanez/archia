package com.melvic.archia.leaf

import com.melvic.archia.Query

data class Term(var field: TermField? = null): Query() {
    operator fun String.invoke(init: TermField.() -> Unit) =
        TermField(this).apply(init)
}

data class TermField(val name: String, var boost: Float? = null) {
    lateinit var value: String
}

