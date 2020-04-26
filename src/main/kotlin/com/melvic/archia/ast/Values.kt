package com.melvic.archia.ast

import com.melvic.archia.ast.fulltext.MatchQueryValue
import com.melvic.archia.ast.leaf.RangeValue
import java.time.LocalDate

typealias Analyzer = String
typealias CutoffFrequency = Double

data class ANumber(val value: Number) : MatchQueryValue, SimpleMSM, RangeValue, DecayFieldType
data class AString(val value: String) : MatchQueryValue
data class ABoolean(val value: Boolean) : MatchQueryValue
data class ADate(val value: LocalDate) : MatchQueryValue, DecayFieldType

sealed class Geo : DecayFieldType
data class GeoObject(val lat: Number, val long: Number) : Geo()
data class GeoString(val lat: Number, val long: Number) : Geo()
data class GeoHash(val hash: String) : Geo()
data class GeoArray(val lat: Double, val long: Double) : Geo()
data class GeoWktPoint(val lat: Double, val long: Double) : Geo()

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