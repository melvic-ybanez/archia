package com.melvic.archia.leaf

import com.melvic.archia.Clause
import com.melvic.archia.Init

open class WithLeaf : Clause() {
    fun match(query: Init<Match>) = initChild(Match(), query)

    fun term(query: Init<Term>) = initChild(Term(), query)
}