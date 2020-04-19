package main.kotlin.com.melvic.archia.ast

import main.kotlin.com.melvic.archia.ast.compound.BoolQuery
import main.kotlin.com.melvic.archia.ast.compound.BoostingQuery
import main.kotlin.com.melvic.archia.ast.leaf.MatchQuery
import main.kotlin.com.melvic.archia.ast.leaf.RangeQuery
import main.kotlin.com.melvic.archia.ast.leaf.TermQuery

interface Builder {
    fun <C : Clause> registerClause(clause: C, init: Init<C>)

    fun term(init: Init<TermQuery>) {
        registerClause(TermQuery(), init)
    }

    fun match(init: Init<MatchQuery>) {
        registerClause(MatchQuery(), init)
    }

    fun range(init: Init<RangeQuery>) {
        registerClause(RangeQuery(), init)
    }

    fun bool(init: Init<BoolQuery>) {
        registerClause(BoolQuery(), init)
    }

    fun boosting(init: Init<BoostingQuery>) {
        registerClause(BoostingQuery(), init)
    }
}

data class ClauseBuilder(var clause: Clause? = null) :
    Builder {
    override fun <C : Clause> registerClause(clause: C, init: Init<C>) {
        this.clause = clause.apply(init)
    }
}

data class ClauseArrayBuilder(val clauses: MutableList<Clause> = mutableListOf()) :
    Builder {
    override fun <C : Clause> registerClause(clause: C, init: Init<C>) {
        clauses.add(clause.apply(init))
    }
}