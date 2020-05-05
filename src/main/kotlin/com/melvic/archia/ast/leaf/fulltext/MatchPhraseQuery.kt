package com.melvic.archia.ast.leaf.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class MatchPhraseQuery : WithField<MatchPhraseField>() {
    override fun initField(name: String): MatchPhraseField {
        return MatchPhraseField(name)
    }
}

class MatchPhraseField(name: String) : Field(name) {
    var query: String by parameters
    var analyzer: Analyzer by parameters
    var zeroTermsQuery: ZeroTermsQuery by parameters
    var slop: Int by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::query)
}