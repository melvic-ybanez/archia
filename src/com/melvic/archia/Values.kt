package com.melvic.archia

import com.melvic.archia.leaf.MatchQueryValue
import com.melvic.archia.leaf.RangeValue
import java.util.*

data class ANumber(val value: Number) : MatchQueryValue, SimpleMSM, RangeValue
data class AString(val value: String) : MatchQueryValue
data class ABoolean(val value: Boolean) : MatchQueryValue
data class ADate(val value: Date) : MatchQueryValue

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
    fun date(value: Date) = ADate(value)

    fun Date.es() = ADate(this)
}