package com.melvic.archia

sealed class Fuzziness {
    object _0 : Fuzziness()
    object _1 : Fuzziness()
    object _2 : Fuzziness()
    data class Auto(val low: Int? = null, val high: Int? = null) : Fuzziness()
}

enum class Rewrite {
    CONSTANT_SCORE,
    CONSTANT_SCORE_BOOLEAN,
    SCORING_BOOLEAN,
    TOP_TERMS_BLENDED_FREQS_N,
    TOP_TERMS_BOOST_N,
    TOP_TERMS_N
}

sealed class MinimumShouldMatch {
    interface Simple

    data class NumberValue(val value: Int) : MinimumShouldMatch(), Simple
    data class Percentage(val value: Int) : MinimumShouldMatch(), Simple
    data class Combination(val value: Int, val simple: Simple) : MinimumShouldMatch()
    data class Multiple(val combinations: List<Combination>) : MinimumShouldMatch()
}