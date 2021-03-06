package com.melvic.archia.ast.leaf.fulltext

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

class MatchQuery : WithShortForm<MatchField, MatchQueryValue>(), WithMatchQueryValue {
    override fun initField(name: String) = MatchField(name)

    override fun MatchField.updateValue(value: MatchQueryValue) {
        this.query = value
    }
}

class MatchField(name: String): Field(name) {
    var query: MatchQueryValue by parameters
    var analyzer: Analyzer by parameters
    var autoGeneratedSynonymsPhraseQuery: Boolean by parameters
    var fuzziness: Fuzziness by parameters
    var maxExpansions: Int by parameters
    var prefixLength: Int by parameters
    var transpositions: Boolean by parameters
    var fuzzyRewrite: Rewrite by parameters
    var lenient: Boolean by parameters
    var operator: Operator by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters
    var zeroTermsQuery: ZeroTermsQuery by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::query)
}

interface MatchQueryValue

interface WithMatchQueryValue : WithText, WithNum, WithBool, WithDate

class MatchAllQuery : Clause() {
    var boost: Boost by parameters
}
class MatchNoneQuery : Clause()

enum class Operator { OR, AND }
enum class ZeroTermsQuery { NONE, ALL }