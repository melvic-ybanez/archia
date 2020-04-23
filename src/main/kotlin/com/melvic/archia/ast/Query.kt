package com.melvic.archia.ast

import com.melvic.archia.interpreter.interpret
import com.melvic.archia.interpreter.toSnakeCase
import kotlin.reflect.KCallable

typealias Init<A> = A.() -> Unit

data class Query(var clause: Clause? = null) {
    fun query(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        clause = builder.clause
    }
}

interface Clause {
    /**
     * Returns the name of the clause in lowercase snake format,
     * removing the "Query" suffix, it one is found.
     */
    fun esName(): String {
        val simpleName = this::class.java.simpleName
        val noPrefix = simpleName.replace("Query", "")
        return noPrefix.toSnakeCase()
    }

    fun <A> param(callable: KCallable<Unit>, value: A): Param<A> {
        return Pair(callable.name, value)
    }
}

typealias MultiClause = MutableList<Clause>

fun buildQuery(init: Init<Query>) = Query().apply(init)

fun evalQuery(init: Init<ClauseBuilder>) = interpret {
    query(init)
}