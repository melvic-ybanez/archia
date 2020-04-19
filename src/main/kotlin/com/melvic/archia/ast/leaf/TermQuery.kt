package main.kotlin.com.melvic.archia.ast.leaf

import main.kotlin.com.melvic.archia.ast.Boost
import main.kotlin.com.melvic.archia.ast.Clause

data class TermQuery(var field: TermField? = null):
    Clause {
    var customProp: Pair<String, String>? = null

    operator fun String.invoke(init: TermField.() -> Unit) {
        field = TermField(this).apply(init)
    }

    infix fun String.to(_value: String) {
        customProp = Pair(this, _value)
        this { value = _value }
    }
}

data class TermField(
    val name: String,
    var value: String? = null,
    var boost: Boost? = null
)

