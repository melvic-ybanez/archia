package com.melvic.archia.ast

import kotlin.reflect.KCallable

typealias Param<A> = Pair<String, A>
typealias ParamList<A> = MutableMap<String, A>

operator fun <A> ParamList<A>.set(callable: KCallable<Unit>, value: A) {
    this[callable.name] = value
}