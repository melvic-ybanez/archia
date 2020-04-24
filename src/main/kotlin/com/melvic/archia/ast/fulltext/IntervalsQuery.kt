package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.MatchField
import kotlin.reflect.KCallable

class IntervalsQuery : WithField<IntervalField>() {
    override fun getField(name: String) = IntervalField(name)
}

class IntervalField(name: String, var rule: Param<IntervalRule>? = null) : Field(name), ParamHelper {
    private inline fun <reified R : IntervalRule> save(init: Init<R>, field: KCallable<Unit>) {
        setProp(init) { this.rule = param(field, it) }
    }

    fun match(init: Init<MatchRule>) {
        save(init, ::match)
    }

    fun prefix(init: Init<PrefixRule>) {
        save(init, ::prefix)
    }

    fun wildcard(init: Init<WildCardRule>) {
        save(init, ::wildcard)
    }

    fun fuzzy(init: Init<FuzzyRule>) {
        save(init, ::fuzzy)
    }

    fun allOf(init: Init<AllOfRule>) {
        save(init, ::allOf)
    }

    fun anyOf(init: Init<AnyOfRule>) {
        save(init, ::anyOf)
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