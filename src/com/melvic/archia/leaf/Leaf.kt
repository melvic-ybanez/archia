package com.melvic.archia.leaf

import com.melvic.archia.Init

interface WithLeaf {
    fun match(query: Init<Match>) = Match().apply(query)

    fun term(query: Init<Term>) = Term().apply(query)
}