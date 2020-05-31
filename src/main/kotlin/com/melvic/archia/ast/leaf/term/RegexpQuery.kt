package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Flag
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class RegexpQuery : WithField<RegexpField>() {
    override fun initField(name: String): RegexpField {
        return RegexpField(name)
    }
}

class RegexpField(name: String) : Field(name) {
    var value: String by parameters
    var flags: Flag by parameters
    var maxDeterminedStates: Int by parameters
    var rewrite: Rewrite by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::value)
}