package com.melvic.archia.ast

import com.melvic.archia.ast.leaf.RangeValue
import com.melvic.archia.ast.leaf.fulltext.MatchQueryValue
import java.time.LocalDate

typealias Analyzer = String
typealias CutoffFrequency = Double
typealias TimeZone = String

data class ANumber(val value: Number) : MatchQueryValue, SimpleMSM, RangeValue
data class AString(val value: String) : MatchQueryValue, DecayFieldType
data class ABoolean(val value: Boolean) : MatchQueryValue
data class ADate(val value: LocalDate) : MatchQueryValue, DecayFieldType

interface WithNum {
    fun num(value: Number) = ANumber(value)

    fun Number.es() = ANumber(this)
}

interface WithText {
    fun text(value: String) = AString(value)

    fun String.es() = AString(this)
}

interface WithBool {
    fun bool(value: Boolean) = ABoolean(value)

    fun Boolean.es() = ABoolean(this)
}

interface WithDate {
    fun date(year: Int, month: Int, day: Int) = ADate(LocalDate.of(year, month, day))

    fun LocalDate.es() = ADate(this)
}