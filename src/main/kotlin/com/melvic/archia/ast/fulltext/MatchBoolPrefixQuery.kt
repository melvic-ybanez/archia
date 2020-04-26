package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.MinimumShouldMatch
import com.melvic.archia.ast.WithField

class MatchBoolPrefixQuery : WithField<MatchBoolPrefixField>() {
    override fun getField(name: String): MatchBoolPrefixField {
        return MatchBoolPrefixField(name)
    }
}

class MatchBoolPrefixField(
    name: String,
    var query: String? = null,
    var analyzer: Analyzer? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var operator: Operator? = null
): Field(name)