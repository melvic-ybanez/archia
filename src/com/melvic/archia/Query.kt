package com.melvic.archia

import com.melvic.archia.interpreter.interpret
import com.melvic.archia.leaf.MatchQuery
import com.melvic.archia.leaf.RangeQuery
import com.melvic.archia.leaf.TermQuery

typealias Init<A> = A.() -> Unit

data class Query(var queryClause: Clause? = null) {
    fun query(init: Init<Clause>) {
        queryClause = Clause().apply(init)
    }
}

open class Clause {
    val children = mutableListOf<Clause>()

    /**
     * Constructs es child and add it to the list of children
     */
    fun <Q : Clause> initChild(child: Q, init: Init<Q>) {
        child.init()
        children.add(child)
    }

    fun match(query: Init<MatchQuery>) = initChild(MatchQuery(), query)

    fun term(query: Init<TermQuery>) = initChild(TermQuery(), query)

    fun range(query: Init<RangeQuery>) = initChild(RangeQuery(), query)
}

open class MultiClause : Clause()

fun buildQuery(init: Init<Query>) = Query().apply(init)

fun runQuery(init: Init<Query>) = buildQuery(init).interpret()