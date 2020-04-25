package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.*
import kotlin.reflect.KCallable

class IntervalsQuery : WithField<IntervalField>() {
    override fun getField(name: String) = IntervalField(name)
}

class IntervalField(
    name: String,
    var rule: Param<IntervalRule>? = null
) : Field(name), ParamHelper, IntervalBuilder {
    private inline fun <reified R : IntervalRule> save(init: Init<R>, field: KCallable<Unit>) {
        setProp(init) { this.rule = param(field, it) }
    }

    override fun match(init: Init<MatchRule>) {
        save(init, ::match)
    }

    override fun prefix(init: Init<PrefixRule>) {
        save(init, ::prefix)
    }

    override fun wildcard(init: Init<WildCardRule>) {
        save(init, ::wildcard)
    }

    override fun fuzzy(init: Init<FuzzyRule>) {
        save(init, ::fuzzy)
    }

    override fun allOf(init: Init<AllOfRule>) {
        save(init, ::allOf)
    }

    override fun anyOf(init: Init<AnyOfRule>) {
        save(init, ::anyOf)
    }
}

class MultiIntervals : IntervalBuilder, ParamHelper {
    val intervals: MutableList<Param<IntervalRule>> = mutableListOf()

    private inline fun <reified R : IntervalRule> addInterval(init: Init<R>, field: KCallable<Unit>) {
        setProp(init) {
            val rule = param(field, it)
            intervals.add(rule)
        }
    }

    override fun match(init: Init<MatchRule>) {
        addInterval(init, ::match)
    }

    override fun prefix(init: Init<PrefixRule>) {
        addInterval(init, ::prefix)
    }

    override fun wildcard(init: Init<WildCardRule>) {
        addInterval(init, ::wildcard)
    }

    override fun fuzzy(init: Init<FuzzyRule>) {
        addInterval(init, ::fuzzy)
    }

    override fun allOf(init: Init<AllOfRule>) {
        addInterval(init, ::allOf)
    }

    override fun anyOf(init: Init<AnyOfRule>) {
        addInterval(init, ::anyOf)
    }
}

sealed class IntervalRule

open class WithAnalyzer : IntervalRule() {
    var analyzer: String? = null
    var useField: String? = null
}

open class IntervalOptions : IntervalRule() {
    var _intervals: MutableList<Param<IntervalRule>>? = null
    var _filter: FilterRule? = null

    fun intervals(init: Init<MultiIntervals>) {
        setProp(init) { _intervals = it.intervals }
    }

    fun filter(init: Init<FilterRule>) {
        setProp(init) { _filter = it }
    }
}

data class MatchRule(
    var query: String? = null,
    var maxGaps: Int? = null,
    var ordered: Boolean? = null,
    var _filter: FilterRule? = null
) : WithAnalyzer() {
    fun filter(init: Init<FilterRule>) {
        setProp(init) { _filter = it }
    }
}

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

class AnyOfRule : IntervalOptions()

data class FilterRule(var query: Param<Clause>? = null): IntervalRule(), ParamHelper, BuilderHelper {
    private fun saveParam(init: Init<ClauseBuilder>, field: KCallable<Unit>) {
        setClause(init) { this.query = param(field, it) }
    }

    fun after(init: Init<ClauseBuilder>) {
        saveParam(init, ::after)
    }

    fun before(init: Init<ClauseBuilder>) {
        saveParam(init, ::before)
    }

    fun containedBy(init: Init<ClauseBuilder>) {
        saveParam(init, ::containedBy)
    }

    fun containing(init: Init<ClauseBuilder>) {
        saveParam(init, ::containing)
    }

    fun notContainedBy(init: Init<ClauseBuilder>) {
        saveParam(init, ::notContainedBy)
    }

    fun notContaining(init: Init<ClauseBuilder>) {
        saveParam(init, ::notContaining)
    }

    fun notOverlapping(init: Init<ClauseBuilder>) {
        saveParam(init, ::notOverlapping)
    }

    fun overlapping(init: Init<ClauseBuilder>) {
        saveParam(init, ::overlapping)
    }

    fun script(init: Init<ClauseBuilder>) {
        saveParam(init, ::script)
    }
}

interface IntervalBuilder {
    fun match(init: Init<MatchRule>)

    fun prefix(init: Init<PrefixRule>)

    fun wildcard(init: Init<WildCardRule>)

    fun fuzzy(init: Init<FuzzyRule>)

    fun allOf(init: Init<AllOfRule>)

    fun anyOf(init: Init<AnyOfRule>)
}