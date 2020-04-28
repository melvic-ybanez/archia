import com.melvic.archia.ast.*
import com.melvic.archia.ast.fulltext.Operator
import kotlin.reflect.KProperty

class QueryStringQuery : Clause(), WithNum {
    var query: String by parameters
    var defaultField: String by parameters
    var allowLeadingWildcard: Boolean by parameters
    var analyzeWildcard: Boolean by parameters
    var analyzer: Analyzer by parameters
    var autoGenerateSynonymsPhraseQuery: Boolean by parameters
    var boost: Boost by parameters
    var defaultOperator: Operator by parameters
    var enablePositionIncrements: Boolean by parameters
    var fields: List<String> by parameters
    var fuzziness: Fuzziness by parameters
    var fuzzyMaxExpansions: Int by parameters
    var fuzzyPrefixLength: Int by parameters
    var fuzzyTranspositions: Boolean by parameters
    var lenient: Boolean by parameters
    var maxDeterminedStates: Int by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters
    var quoteAnalyzer: Analyzer by parameters
    var phraseSlop: Int by parameters
    var quoteFieldSuffix: String by parameters
    var rewrite: Rewrite by parameters
    var timezone: TimeZone by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::query)
}