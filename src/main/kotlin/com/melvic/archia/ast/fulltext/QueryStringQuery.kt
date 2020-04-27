import com.melvic.archia.ast.*
import com.melvic.archia.ast.fulltext.Operator

data class QueryStringQuery(
    var query: String? = null,
    var defaultField: String? = null,
    var allowLeadingWildcard: Boolean? = null,
    var analyzeWildcard: Boolean? = null,
    var analyzer: Analyzer? = null,
    var autoGenerateSynonymsPhraseQuery: Boolean? = null,
    var boost: Boost? = null,
    var defaultOperator: Operator? = null,
    var enablePositionIncrements: Boolean? = null,
    var fields: List<String>? = null,
    var fuzziness: Fuzziness? = null,
    var fuzzyMaxExpansions: Int? = null,
    var fuzzyPrefixLength: Int? = null,
    var fuzzyTranspositions: Boolean? = null,
    var lenient: Boolean? = null,
    var maxDeterminedStates: Int? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null,
    var quoteAnalyzer: Analyzer? = null,
    var phraseSlop: Int? = null,
    var quoteFieldSuffix: String? = null,
    var rewrite: Rewrite? = null,
    var timezone: TimeZone? = null
) : Clause(), WithNum