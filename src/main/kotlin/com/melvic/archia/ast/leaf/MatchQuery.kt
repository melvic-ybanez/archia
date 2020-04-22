package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.*

data class MatchQuery(var field: MatchField? = null) : Clause {
    /**
     * Constructs an elasticsearch match field from a string
     */
    operator fun String.invoke(init: Init<MatchField>) {
        field = MatchField(this).apply(init)
    }
}

data class MatchField(
    val name: String,
    var query: MatchQueryValue? = null,
    var analyzer: String? = null,
    var autoGeneratedSynonymsPhraseQuery: Boolean? = null,
    var fuzziness: Fuzziness? = null,
    var maxExpansions: Int? = null,
    var prefixLength: Int? = null,
    var transpositions: Boolean? = null,
    var fuzzyRewrite: Rewrite? = null,
    var lenient: Boolean? = null,
    var operator: Operator? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var zeroTermsQuery: ZeroTermsQuery? = null
): WithText, WithNum, WithBool, WithDate

interface MatchQueryValue

data class MatchAllQuery(var boost: Boost? = null) : Clause
class MatchNoneQuery : Clause

enum class Operator { OR, AND }
enum class ZeroTermsQuery { NONE, ALL }