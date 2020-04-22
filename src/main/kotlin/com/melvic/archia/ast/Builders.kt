package com.melvic.archia.ast

import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.ast.leaf.*

interface Builder {
    fun <C : Clause> registerClause(clause: C, init: Init<C>)

    fun term(init: Init<TermQuery>) {
        registerClause(TermQuery(), init)
    }

    fun match(init: Init<MatchQuery>) {
        registerClause(MatchQuery(), init)
    }

    fun matchAll(init: Init<MatchAllQuery>) {
        registerClause(MatchAllQuery(), init)
    }

    fun matchNone(init: Init<MatchNoneQuery>) {
        registerClause(MatchNoneQuery(), init)
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

    fun constantScore(init: Init<ConstantScoreQuery>) {
        registerClause(ConstantScoreQuery(), init)
    }

    fun disMax(init: Init<DisMaxQuery>) {
        registerClause(DisMaxQuery(), init)
    }

    fun functionScore(init: Init<FunctionScoreQuery>) {
        registerClause(FunctionScoreQuery(), init)
    }

    fun intervals(init: Init<IntervalsQuery>) {
        registerClause(IntervalsQuery(), init)
    }
}

data class ClauseBuilder(var clause: Clause? = null) : Builder {
    override fun <C : Clause> registerClause(clause: C, init: Init<C>) {
        this.clause = clause.apply(init)
    }
}

data class ClauseArrayBuilder(val clauses: MutableList<Clause> = mutableListOf()) : Builder {
    override fun <C : Clause> registerClause(clause: C, init: Init<C>) {
        clauses.add(clause.apply(init))
    }
}