package com.melvic.archia.ast

import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.IntervalsQuery
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.identity
import com.melvic.archia.script.Script
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Houses the query clause constructors
 */
interface Builder {
    fun <C : Clause> save(clause: C)

    private inline fun <reified C : Clause> save(init: Init<C>) {
        setProp(init) { save(it) }
    }

    fun term(termInit: Init<TermQuery>) {
        save(termInit)
    }

    fun match(matchInit: Init<MatchQuery>) {
        save(matchInit)
    }

    fun matchAll(matchAllInit: Init<MatchAllQuery>) {
        save(matchAllInit)
    }

    fun matchNone(matchNoneInit: Init<MatchNoneQuery>) {
        save(matchNoneInit)
    }

    fun range(rangeInit: Init<RangeQuery>) {
        save(rangeInit)
    }

    fun bool(boolInit: Init<BoolQuery>) {
        save(boolInit)
    }

    fun boosting(boostingInit: Init<BoostingQuery>) {
        save(boostingInit)
    }

    fun constantScore(constantScoreInit: Init<ConstantScoreQuery>) {
        save(constantScoreInit)
    }

    fun disMax(disMaxInit: Init<DisMaxQuery>) {
        save(disMaxInit)
    }

    fun functionScore(functionScoreInit: Init<FunctionScoreQuery>) {
        save(functionScoreInit)
    }

    fun intervals(intervalsInit: Init<IntervalsQuery>) {
        save(intervalsInit)
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
    val constructor = cls.primaryConstructor!!.callBy(emptyMap())
    val builder = constructor.apply(init)
    set(map(builder))
}

inline fun <reified B : Any, reified C> build(init: Init<B>, map: (B) -> C, set: (C) -> Unit) {
    build(B::class, init, map, set)
}

inline fun <reified P : Any> setProp(init: Init<P>, set: (P) -> Unit) {
    build(init, ::identity) { set(it) }
}