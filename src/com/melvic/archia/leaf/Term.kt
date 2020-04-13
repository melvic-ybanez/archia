package com.melvic.archia.leaf

import com.melvic.archia.Query

class Term: Query() {
    var field: TermField? = null

    operator fun String.invoke(init: TermField.() -> Unit) =
        TermField(this).apply(init)
}

data class TermField(val name: String) {
    lateinit var value: String

    var boost: Float? = null
}

