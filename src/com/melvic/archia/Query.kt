package com.melvic.archia

typealias Init<A> = A.() -> Unit

open class Query {
    private val children = mutableListOf<Query>()

    /**
     * Constructs a child and add it to the list of children
     */
    fun <Q : Query> addChild(child: Q, init: Init<Q>) {
        child.init()
        children.add(child)
    }

    fun query(init: Init<QueryContext>) = QueryContext().apply(init)
}

fun buildQuery(init: Init<Query>) = Query().apply(init)