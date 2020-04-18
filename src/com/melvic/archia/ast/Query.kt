package com.melvic.archia.ast

typealias Init<A> = A.() -> Unit

data class Query(var queryClause: Clause? = null) {
    fun query(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        queryClause = builder.clause
    }
}

interface Clause
typealias MultiClause = MutableList<Clause>

fun buildQuery(init: Init<Query>) = Query().apply(init)