package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField

class MatchPhrasePrefixQuery : WithField<MatchPhrasePrefixField>() {
    override fun getField(name: String): MatchPhrasePrefixField {
        return MatchPhrasePrefixField(name)
    }
}

class MatchPhrasePrefixField(
    name: String,
    var query: String? = null,
    var analyzer: Analyzer? = null,
    var maxExpansions: Int? = null,
    var slop: Int? = null,
    var zeroTermsQuery: ZeroTermsQuery? = null
) : Field(name)