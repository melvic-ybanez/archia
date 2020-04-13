package com.melvic.archia.leaf

interface WithLeaf {
    fun match(query: Match.() -> Unit) = Match().apply(query)

    fun term(query: Term.() -> Unit) = Term().apply(query)
}