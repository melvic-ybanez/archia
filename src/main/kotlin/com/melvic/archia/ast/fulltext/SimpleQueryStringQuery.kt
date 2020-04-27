package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.MinimumShouldMatch
import kotlin.reflect.KProperty

data class SimpleQueryStringQuery(
    var query: String? = null,
    var fields: List<String>? = null,
    var defaultOperator: Operator? = null,
    var allFields: Boolean? = null,
    var analyzeWildCard: Boolean? = null,
    var analyzer: Analyzer? = null,
    var autoGenerateSynonymsPhraseQuery: Boolean? = null,
    var flags: Flag? = null,
    var fuzzyMaxExpansions: Int? = null,
    var fuzzyPrefixLength: Int? = null,
    var fuzzyTranspositions: Boolean? = null,
    var lenient: Boolean? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var quoteFieldSuffix: String? = null
)

enum class Flag {
    ALL, AND, ESCAPE, FUZZY, NEAR, NONE, NOT, OR, PHRASE, PRECEDENCE,
    PREFIX, SLOP, WHITESPACE
}

infix fun Flag.or(flag: Flag): List<Flag> = listOf(this, flag)