package com.melvic.archia.ast

import com.melvic.archia.interpreter.esNameFormat
import com.melvic.archia.interpreter.toSnakeCase
import kotlin.reflect.KCallable

typealias Param<A> = Pair<String, A>
typealias ParamList<A> = MutableMap<String, A>

operator fun <A> ParamList<A>.set(callable: KCallable<Unit>, value: A) {
    this[callable.name] = value
}

interface ParamHelper {
    fun <A> param(callable: KCallable<Unit>, value: A): Param<A> {
        return Pair(callable.esNameFormat(), value)
    }
}