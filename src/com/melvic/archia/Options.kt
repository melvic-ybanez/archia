package com.melvic.archia

sealed class Fuzziness {
    object _0 : Fuzziness()
    object _1 : Fuzziness()
    object _2 : Fuzziness()
    data class Auto(val distances: Pair<Int, Int>? = null) : Fuzziness()
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

    data class Number(val value: Int) : MinimumShouldMatch(), Simple
    data class Percent(val value: Int) : MinimumShouldMatch(), Simple
    data class Combination(val value: Int, val simple: Simple) : MinimumShouldMatch()
    data class Multiple(val values: List<Combination>) : MinimumShouldMatch()
}

enum class DateFormat {
    EPOCH_MILLIS, EPOCH_SECOND, DATE_OPTIONAL_TIME, STRICT_DATE_OPTIONAL_TIME,
    BASIC_DATE, BASIC_DATE_TIME, BASIC_DATE_TIME_NO_MILLIS, BASIC_ORDINAL_DATE,
    BASIC_ORDINAL_DATE_TIME, BASIC_ORDINAL_DATE_TIME_NO_MILLIS, BASIC_TIME, BASIC_TIME_NO_MILLIS,
    BASIC_T_TIME, BASIC_T_TIME_NO_MILLIS, BASIC_WEEK_DATE, STRICT_BASIC_WEEK_DATE,
    BASIC_WEEK_DATE_TIME, STRICT_BASIC_WEEK_DATE_TIME, BASIC_WEEK_DATE_TIME_NO_MILLIS,
    STRICT_BASIC_WEEK_DATE_TIME_NO_MILLIS, DATE, STRICT_DATE, DATE_HOUR, STRICT_DATE_HOUR,
    DATE_HOUR_MINUTE, STRICT_DATE_HOUR_MINUTE, DATE_HOUR_MINUTE_SECOND, STRICT_DATE_HOUR_MINUTE_SECOND,
    DATE_HOUR_MINUTE_SECOND_FRACTION, STRICT_DATE_HOUR_MINUTE_SECOND_FRACTION, DATE_HOUR_MINUTE_SECOND_MILLIS,
    STRICT_DATE_HOUR_MINUTE_SECOND_MILLIS, DATE_TIME, STRICT_DATE_TIME, DATE_TIME_NO_MILLIS,
    STRICT_DATE_TIME_NO_MILLIS, HOUR, STRICT_HOUR, HOUR_MINUTE, STRICT_HOUR_MINUTE, HOUR_MINUTE_SECOND,
    STRICT_HOUR_MINUTE_SECOND, HOUR_MINUTE_SECOND_FRACTION, STRICT_HOUR_MINUTE_SECOND_FRACTION,
    HOUR_MINUTE_SECOND_MILLIS, STRICT_HOUR_MINUTE_SECOND_MILLIS, ORDINAL_DATE, STRICT_ORDINAL_DATE,
    ORDINAL_DATE_TIME, STRICT_ORDINAL_DATE_TIME, ORDINAL_DATE_TIME_NO_MILLIS, STRICT_ORDINAL_DATE_TIME_NO_MILLIS,
    TIME, STRICT_TIME, TIME_NO_MILLIS, STRICT_TIME_NO_MILLIS, T_TIME, STRICT_T_TIME, T_TIME_NO_MILLIS,
    STRICT_T_TIME_NO_MILLIS, WEEK_DATE, STRICT_WEEK_DATE, WEEK_DATE_TIME, STRICT_WEEK_DATE_TIME,
    WEEK_DATE_TIME_NO_MILLIS, STRICT_WEEK_DATE_TIME_NO_MILLIS, WEEKYEAR, STRICT_WEEKYEAR,
    WEEKYEAR_WEEK, STRICT_WEEKYEAR_WEEK, WEEKYEAR_WEEK_DAY, STRICT_WEEKYEAR_WEEK_DAY, YEAR,
    STRICT_YEAR, YEAR_MONTH, STRICT_YEAR_MONTH, YEAR_MONTH_DAY, STRICT_YEAR_MONTH_DAY
}