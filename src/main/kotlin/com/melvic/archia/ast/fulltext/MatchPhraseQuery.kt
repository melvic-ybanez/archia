package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField

class MatchPhraseQuery : WithField<MatchPhraseField>() {
    override fun getField(name: String): MatchPhraseField {
        return MatchPhraseField(name)
    }
}

class MatchPhraseField(
    name: String,
    var query: String? = null,
    var analyzer: Analyzer? = null,
    var zeroTermsQuery: ZeroTermsQuery? = null
) : Field(name)