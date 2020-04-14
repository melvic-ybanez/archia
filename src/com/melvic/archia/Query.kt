package com.melvic.archia

import com.melvic.archia.leaf.WithLeaf

typealias Init<A> = A.() -> Unit

data class Query(var queryClause: QueryClause? = null) {
    fun query(init: Init<QueryClause>) {
        queryClause = QueryClause().apply(init)
    }
}

open class Clause {
    val children = mutableListOf<Clause>()

    /**
     * Constructs a child and add it to the list of children
     */
    fun <Q : Clause> initChild(child: Q, init: Init<Q>) {
        child.init()
        children.add(child)
    }
}

class QueryClause : WithLeaf()

fun buildQuery(init: Init<Query>) = Query().apply(init)