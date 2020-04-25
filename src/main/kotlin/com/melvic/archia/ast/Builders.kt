package com.melvic.archia.ast

import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.fulltext.*
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.identity
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

    fun term(init: Init<TermQuery>) = save(init)

    fun match(init: Init<MatchQuery>) = save(init)

    fun matchAll(init: Init<MatchAllQuery>) = save(init)

    fun matchNone(init: Init<MatchNoneQuery>) = save(init)

    fun range(init: Init<RangeQuery>) = save(init)

    fun bool(init: Init<BoolQuery>) = save(init)

    fun boosting(init: Init<BoostingQuery>) = save(init)

    fun constantScore(init: Init<ConstantScoreQuery>) = save(init)

    fun disMax(init: Init<DisMaxQuery>) = save(init)

    fun functionScore(init: Init<FunctionScoreQuery>) = save(init)

    fun intervals(init: Init<IntervalsQuery>) = save(init)

    fun matchBoolPrefix(init: Init<MatchBoolPrefixQuery>) = save(init)

    fun matchPhrase(init: Init<MatchPhraseQuery>) = save(init)
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