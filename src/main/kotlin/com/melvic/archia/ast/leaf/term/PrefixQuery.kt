package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.ast.WithField
import com.melvic.archia.ast.WithShortForm
import kotlin.reflect.KProperty

class PrefixQuery : WithShortForm<PrefixField, String>() {
    override fun initField(name: String) = PrefixField(name)

    override fun PrefixField.updateValue(value: String) {
        this.value = value
    }

}

class PrefixField(name: String) : Field(name) {
    var value: String by parameters
    var rewrite: Rewrite by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::value)
}