package com.melvic.archia.ast.leaf.fulltext

import com.melvic.archia.ast.*
import com.melvic.archia.script.Script
import kotlin.reflect.KCallable

class IntervalsQuery : WithField<IntervalField>() {
    override fun initField(name: String) = IntervalField(name)
}

class IntervalField(name: String) : Field(name), ParamHelper, IntervalBuilder {
    private var rule: Param<IntervalRule> by parameters

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

open class IntervalRule : TreeNode()

open class WithAnalyzer : IntervalRule() {
    var analyzer: String by parameters
    var useField: String by parameters
}

open class IntervalOptions : IntervalRule() {
    private var intervals: MutableList<Param<IntervalRule>> by parameters
    var filter: FilterRule by parameters

    fun intervals(init: Init<MultiIntervals>) {
        setProp(init) { intervals = it.intervals }
    }

    fun filter(init: Init<FilterRule>) {
        setProp(init) { filter = it }
    }
}

class MatchRule : WithAnalyzer() {
    var query: String by parameters
    var maxGaps: Int by parameters
    var ordered: Boolean by parameters
    var filter: FilterRule by parameters

    fun filter(init: Init<FilterRule>) {
        setProp(init) { filter = it }
    }
}

class PrefixRule: WithAnalyzer() {
    var prefix: String by parameters
}

class WildCardRule : WithAnalyzer() {
    var pattern: String by parameters
}

class FuzzyRule : WithAnalyzer() {
    var term: String by parameters
    var prefixLength: String by parameters
    var transpositions: Boolean by parameters
    var fuzziness: Fuzziness by parameters
}

class AllOfRule : IntervalOptions() {
    var maxGaps: Int by parameters
    var ordered: Boolean by parameters
}

class AnyOfRule : IntervalOptions()

class FilterRule: IntervalRule(), ParamHelper, BuilderHelper {
    var query: Param<Clause> by parameters

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

    fun script(init: Init<Script>) {
        setProp(init) { query = param(::script, it) }
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