package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.*
import com.melvic.archia.script.Script
import kotlin.reflect.KProperty

class TermsSetQuery : WithField<TermsSetField>() {
    override fun initField(name: String): TermsSetField {
        return TermsSetField(name)
    }
}

class TermsSetField(name: String) : Field(name) {
    var terms: List<String> by parameters
    var minimumShouldMatchField: String by parameters
    private var minimumShouldMatchScript: Script by parameters
    var boost: Boost by parameters

    fun minimumShouldMatchScript(init: Init<Script>) {
        setProp(init) { minimumShouldMatchScript = it }
    }

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::terms)
}