package com.melvic.archia.ast

import com.melvic.archia.output.JsonString
import com.melvic.archia.output.JsonValue

sealed class Flag
object All : Flag()
object Complement : Flag()
object Interval : Flag()
object Intersection : Flag()
object AnyString : Flag()
data class MultiFlag(val left: Flag, val right: Flag) : Flag()

infix fun Flag.or(that: Flag): Flag {
    return MultiFlag(this, that)
}

fun Flag.interpret(): JsonValue {
    val value = when (this) {
        is MultiFlag -> "${this.left.interpret()}|${this.right.interpret()}"
        else -> this::class.java.simpleName.toUpperCase()
    }
    return JsonString(value)
}