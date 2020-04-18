package com.melvic.archia.ast

import com.melvic.archia.ast.leaf.*
import com.melvic.archia.interpreter.interpret

typealias Init<A> = A.() -> Unit

data class Query(var queryClause: Clause? = null) {
    fun query(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        queryClause = builder.clause
    }
}

interface Clause
typealias MultiClause = List<Clause>

fun buildQuery(init: Init<Query>) = Query().apply(init)

fun runQuery(init: Init<Query>) = buildQuery(init).interpret()