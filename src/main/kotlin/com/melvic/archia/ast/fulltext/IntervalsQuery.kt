package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.*

class IntervalsQuery : WithField<IntervalField>()

class IntervalField(name: String, var rule: IntervalRule? = null) : Field(name) {
    private fun <R : IntervalRule> save(rule: R) {
        this.rule = rule
    }

    fun match(init: Init<MatchRule>) {
        setProp(init, ::save)
    }

    fun prefix(init: Init<PrefixRule>) {
        setProp(init, ::save)
    }

    fun wildcard(init: Init<WildCardRule>) {
        setProp(init, ::save)
    }

    fun fuzzy(init: Init<FuzzyRule>) {
        setProp(init, ::save)
    }

    fun allOf(init: Init<AllOfRule>) {
        setProp(init, ::save)
    }

    fun anyOf(init: Init<AnyOfRule>) {
        setProp(init, ::save)
    }
}

sealed class IntervalRule

open class WithAnalyzer : IntervalRule() {
    var analyzer: String? = null
    var useField: String? = null
}

open class IntervalOptions : IntervalRule() {
    var _intervals: MutableList<IntervalsQuery>? = null
    var _filter: FilterRule? = null

    fun intervals(init: Init<IntervalsQuery>) {
        setProp(init) {
            _intervals?.add(it) ?: run {
                _intervals = mutableListOf(it)
            }
        }
    }

    fun filter(init: Init<FilterRule>) {
        setProp(init) { _filter = it }
    }
}

data class MatchRule(
    var query: String? = null,
    var maxGaps: Int? = null,
    var ordered: Boolean? = null,
    var filter: FilterRule? = null
) : WithAnalyzer()

data class PrefixRule(var prefix: String? = null): WithAnalyzer()

data class WildCardRule(var pattern: String? = null) : WithAnalyzer()

data class FuzzyRule(
    var term: String? = null,
    var prefixLength: String? = null,
    var transpositions: Boolean? = null,
    var fuzziness: Fuzziness? = null
) : WithAnalyzer()

data class AllOfRule(
    var maxGaps: Int? = null,
    var ordered: Boolean? = null
) : IntervalOptions()

object AnyOfRule : IntervalOptions()

data class FilterRule(var query: Clause? = null): IntervalRule(), BuilderHelper {
    private fun save(query: Clause) {
        this.query = query
    }

    fun after(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun before(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun containedBy(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun containing(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun notContainedBy(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun notContaining(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun notOverlapping(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun overlapping(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }

    fun script(init: Init<ClauseBuilder>) {
        setClause(init, ::save)
    }
}