package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class MatchPhrasePrefixQuery : WithField<MatchPhrasePrefixField>() {
    override fun initField(name: String): MatchPhrasePrefixField {
        return MatchPhrasePrefixField(name)
    }
}

class MatchPhrasePrefixField(name: String) : Field(name) {
    var query: String by parameters
    var analyzer: Analyzer by parameters
    var maxExpansions: Int by parameters
    var slop: Int by parameters
    var zeroTermsQuery: ZeroTermsQuery by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::query)
}