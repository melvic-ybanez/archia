package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.Analyzer
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Fuzziness
import com.melvic.archia.script.Script

data class IntervalsQuery(var field: IntervalField? = null) : Clause

data class IntervalField(
    val name: String,
    var _match: MatchRule? = null,
    var _prefix: PrefixRule? = null,
    var _wildcard: WildCardRule? = null,
    var _fuzzy: FuzzyRule? = null,
    var _allOf: AllOfRule? = null,
    var _anyOf: AnyOfRule? = null
)

sealed class IntervalRule

open class WithAnalyzer : IntervalRule() {
    var analyzer: String? = null
    var useField: String? = null
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
    var intervals: List<IntervalRule>? = null,
    var maxGaps: Int? = null,
    var ordered: Boolean? = null,
    var filter: FilterRule? = null
) : IntervalRule()

data class AnyOfRule(
    var intervals: IntervalRule? = null,
    var filter: FilterRule? = null
) : IntervalRule()

data class FilterRule(
    var after: Clause? = null,
    var before: Clause? = null,
    var containedBy: Clause? = null,
    var containing: Clause? = null,
    var notContainedBy: Clause? = null,
    var notContaining: Clause? = null,
    var notOverlapping: Clause? = null,
    var overlapping: Clause? = null,
    var script: Script? = null
) : IntervalRule()