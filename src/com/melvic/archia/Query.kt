package com.melvic.archia

import com.melvic.archia.interpreter.interpret
import com.melvic.archia.leaf.Match
import com.melvic.archia.leaf.Term

typealias Init<A> = A.() -> Unit

data class Query(var queryClause: Clause? = null) {
    fun query(init: Init<Clause>) {
        queryClause = Clause().apply(init)
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

    fun match(query: Init<Match>) = initChild(Match(), query)

    fun term(query: Init<Term>) = initChild(Term(), query)
}

open class MultiClause : Clause()

fun buildQuery(init: Init<Query>) = Query().apply(init)

fun runQuery(init: Init<Query>) = buildQuery(init).interpret()