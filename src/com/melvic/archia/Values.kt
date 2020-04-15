package com.melvic.archia

import com.melvic.archia.leaf.MatchQueryValue
import java.util.*

data class ANumber(val value: Number) : MatchQueryValue, SimpleMSM
data class AString(val value: String) : MatchQueryValue
data class ABoolean(val value: Boolean) : MatchQueryValue
data class ADate(val value: Date) : MatchQueryValue

interface WithNum {
    fun num(value: Number) = ANumber(value)
}

interface WithText {
    fun text(value: String) = AString(value)
}

interface WithBool {
    fun bool(value: Boolean) = ABoolean(value)
}

interface WithDate {
    fun date(value: Date) = ADate(value)
}