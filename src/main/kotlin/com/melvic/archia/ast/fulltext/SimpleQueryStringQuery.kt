package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.MinimumShouldMatch

class SimpleQueryStringQuery: Clause() {
    var query: String by parameters
    var fields: List<String> by parameters
    var defaultOperator: Operator by parameters
    var allFields: Boolean by parameters
    var analyzeWildCard: Boolean by parameters
    var analyzer: Analyzer by parameters
    var autoGenerateSynonymsPhraseQuery: Boolean by parameters
    var flags: Flag by parameters
    var fuzzyMaxExpansions: Int by parameters
    var fuzzyPrefixLength: Int by parameters
    var fuzzyTranspositions: Boolean by parameters
    var lenient: Boolean by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters
    var quoteFieldSuffix: String by parameters

    override val requiredParams = listOf(::query)
}

enum class Flag {
    ALL, AND, ESCAPE, FUZZY, NEAR, NONE, NOT, OR, PHRASE, PRECEDENCE,
    PREFIX, SLOP, WHITESPACE
}

infix fun Flag.or(flag: Flag): List<Flag> = listOf(this, flag)