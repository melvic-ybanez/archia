package com.melvic.archia.ast

import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.identity
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Houses the query clause constructors
 */
interface Builder {
    fun <C : Clause> save(clause: C)

    fun term(termInit: Init<TermQuery>) {
        setProp(termInit) { save(it) }
    }

    fun match(matchInit: Init<MatchQuery>) {
        setProp(matchInit) { save(it) }
    }

    fun matchAll(matchAllInit: Init<MatchAllQuery>) {
        setProp(matchAllInit) { save(it) }
    }

    fun matchNone(matchNoneInit: Init<MatchNoneQuery>) {
        setProp(matchNoneInit) { save(it) }
    }

    fun range(rangeInit: Init<RangeQuery>) {
        setProp(rangeInit) { save(it) }
    }

    fun bool(boolInit: Init<BoolQuery>) {
        setProp(boolInit) { save(it) }
    }

    fun boosting(boostingInit: Init<BoostingQuery>) {
        setProp(boostingInit) { save(it) }
    }

    fun constantScore(constantScoreInit: Init<ConstantScoreQuery>) {
        setProp(constantScoreInit) { save(it) }
    }

    fun disMax(disMaxInit: Init<DisMaxQuery>) {
        setProp(disMaxInit) { save(it) }
    }

    fun functionScore(functionScoreInit: Init<FunctionScoreQuery>) {
        setProp(functionScoreInit) { save(it) }
    }

    fun intervals(intervalsInit: Init<IntervalsQuery>) {
        setProp(intervalsInit) { save(it) }
    }
}

data class ClauseBuilder(var clause: Clause? = null) : Builder {
    override fun <C : Clause> save(clause: C) {
        this.clause = clause
    }
}

data class ClauseArrayBuilder(val clauses: MutableList<Clause> = mutableListOf()) : Builder {
    override fun <C : Clause> save(clause: C) {
        clauses.add(clause)
    }
}

interface BuilderHelper {
    fun setClauseArray(init: Init<ClauseArrayBuilder>, set: (MultiClause) -> Unit) {
        build(init, { it.clauses }) { set(it) }
    }

    fun setClause(init: Init<ClauseBuilder>, set: (Clause) -> Unit) {
        build(init, { it.clause }) { it?.let { set(it) } }
    }
}

/**
 * Builds an instance of any type B that has a parameterless primary
 * constructor.
 * Note: Do not use apply this function to a type without a parameterless
 * constructor
 *
 * @param init The initializer for the builder
 * @param map A function that transform the instance
 * @param set A function that saves the instance
 */
inline fun <B : Any, C> build(cls: KClass<B>, init: Init<B>, map: (B) -> C, set: (C) -> Unit) {
    cls.primaryConstructor?.let {
        val constructor = it.call()
        val builder = constructor.apply(init)
        set(map(builder))
    }
}

inline fun <reified B : Any, reified C> build(init: Init<B>, f: (B) -> C, set: (C) -> Unit) {
    build(B::class, init, f, set)
}

inline fun <reified P> setProp(init: Init<P>, set: (P) -> Unit) {
    build(init, ::identity) { set(it) }
}