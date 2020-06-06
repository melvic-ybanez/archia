package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class TermsQuery : WithField<LookupField>() {
    private var terms: Param<List<String>> by parameters
    var boost: Boost by parameters

    operator fun String.invoke(vararg term: String) {
        terms = Pair(this, term.toList())
    }

    override fun initField(name: String): LookupField {
        return LookupField(name);
    }

    override val requiredParams: List<KProperty<Any>>
        get() = emptyList()
}

class LookupField(name: String) : Field(name) {
    var index: String by parameters
    var id: String by parameters
    var path: String by parameters
    var routing: String by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::index, ::id, ::path)
}