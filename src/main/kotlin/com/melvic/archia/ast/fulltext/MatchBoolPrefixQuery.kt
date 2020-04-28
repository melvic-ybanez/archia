package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.MinimumShouldMatch
import com.melvic.archia.ast.WithField
import kotlin.reflect.KProperty

class MatchBoolPrefixQuery : WithField<MatchBoolPrefixField>() {
    override fun initField(name: String): MatchBoolPrefixField {
        return MatchBoolPrefixField(name)
    }
}

class MatchBoolPrefixField(name: String): Field(name) {
    var query: String by parameters
    var analyzer: Analyzer by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters
    var operator: Operator by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::query)
}