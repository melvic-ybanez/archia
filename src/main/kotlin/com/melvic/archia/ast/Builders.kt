package com.melvic.archia.ast

import QueryStringQuery
import com.melvic.archia.ast.compound.*
import com.melvic.archia.ast.leaf.RangeQuery
import com.melvic.archia.ast.leaf.term.TermQuery
import com.melvic.archia.ast.leaf.fulltext.*
import com.melvic.archia.ast.leaf.geo.*
import com.melvic.archia.ast.leaf.joining.HasChildQuery
import com.melvic.archia.ast.leaf.joining.HasParentQuery
import com.melvic.archia.ast.leaf.joining.NestedQuery
import com.melvic.archia.ast.leaf.joining.ParentIdQuery
import com.melvic.archia.ast.leaf.term.ExistsQuery
import com.melvic.archia.ast.leaf.term.FuzzyQuery
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

    fun matchPhrasePrefix(init: Init<MatchPhrasePrefixQuery>) = save(init)

    fun multiMatch(init: Init<MultiMatchQuery>) = save(init)

    fun common(init: Init<CommonTermsQuery>) = save(init)

    fun queryString(init: Init<QueryStringQuery>) = save(init)
    
    fun simpleQueryString(init: Init<SimpleQueryStringQuery>) = save(init)

    fun geoBoundingBox(init: Init<GeoBoundingBoxQuery>) = save(init)

    fun geoDistance(init: Init<GeoDistanceQuery>) = save(init)

    fun geoPolygon(init: Init<GeoPolygonQuery>) = save(init)

    fun geoShape(init: Init<GeoShapeQuery>) = save(init)

    fun shape(init: Init<ShapeQuery>) = save(init)

    fun nested(init: Init<NestedQuery>) = save(init)

    fun hasChild(init: Init<HasChildQuery>) = save(init)

    fun hasParent(init: Init<HasParentQuery>) = save(init)

    fun parentId(init: Init<ParentIdQuery>) = save(init)

    fun exists(init: Init<ExistsQuery>) = save(init)

    fun fuzzy(init: Init<FuzzyQuery>) = save(init)
}

/**
 * A builder for a single query. Use this for types that hold query objects.
 */
class ClauseBuilder : Builder {
    var clause: Clause = Clause()

    override fun <C : Clause> save(clause: C) {
        this.clause = clause
    }
}

/**
 * A builder for multiple queries.
 */
data class ClauseArrayBuilder(val clauses: MutableList<Clause> = mutableListOf()) : Builder {
    override fun <C : Clause> save(clause: C) {
        clauses.add(clause)
    }
}

/**
 * Contains functions for saving the clauses saved by the builders. Typically, this
 * should be used as a mixin for Clause sub-types that contain fields with the Clause
 * or MultiClause type.
 */
interface BuilderHelper {
    fun setClauseArray(init: Init<ClauseArrayBuilder>, set: (MultiClause) -> Unit) {
        build(init, { it.clauses }) { set(it) }
    }

    fun setClause(init: Init<ClauseBuilder>, set: (Clause) -> Unit) {
        build(init, { it.clause }) { set(it) }
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