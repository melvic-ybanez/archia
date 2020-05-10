package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Fuzziness
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class FuzzyQuery : WithField<FuzzyField>() {
    override fun initField(name: String): FuzzyField {
        return FuzzyField(name)
    }
}

class FuzzyField(name: String) : Field(name) {
    var value: String by parameters
    var fuzziness: Fuzziness by parameters
    var maxExpansions: Int by parameters
    var prefixLength: Int by parameters
    var transpositions: Boolean by parameters
    var rewrite: Rewrite by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::value)
}