package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*

class BoolQuery: Clause(), WithNum, BuilderHelper {
    var must: OneOrMore<Clause> by parameters
    var filter: OneOrMore<Clause> by parameters
    var should: OneOrMore<Clause> by parameters
    var mustNot: OneOrMore<Clause> by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters
    var boost: Boost by parameters

    private fun setOneOrMore(
        init: Init<ClauseArrayBuilder>,
        f: (OneOrMore<Clause>) -> Unit
    ) {
        setClauseArray(init) { f(OneOrMore(it)) }
    }

    fun must(init: Init<ClauseArrayBuilder>) {
        setOneOrMore(init) { must = it }
    }

    fun filter(init: Init<ClauseArrayBuilder>) {
        setOneOrMore(init) { filter = it }
    }

    fun should(init: Init<ClauseArrayBuilder>) {
        setOneOrMore(init) { should = it }
    }

    fun mustNot(init: Init<ClauseArrayBuilder>) {
        setOneOrMore(init) { mustNot = it }
    }
}