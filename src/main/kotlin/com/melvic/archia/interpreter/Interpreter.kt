package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.BoolQuery
import com.melvic.archia.ast.compound.BoostingQuery
import com.melvic.archia.ast.compound.ConstantScoreQuery
import com.melvic.archia.ast.compound.DisMaxQuery
import com.melvic.archia.ast.leaf.MatchQuery
import com.melvic.archia.ast.leaf.RangeQuery
import com.melvic.archia.ast.leaf.TermQuery
import com.melvic.archia.output.*
import kotlin.reflect.KCallable

typealias Evaluation = Result<JsonValue>

fun interpret(init: Init<Query>) = buildQuery(init).interpret()

fun Query.interpret(): Evaluation {
    val output = this.queryClause?.interpret() ?: missingField(this::query)

    return when (output) {
        is Failed -> output
        is Success<*> -> json {
            "query" to output.value()
        }.success()
    }
}

fun Clause.interpret(parent: JsonValue = json {}): Evaluation {
    val objectOrEmpty = if (parent is JsonObject) parent else json {}

    return when (this) {
        is TermQuery -> interpret(objectOrEmpty)
        is MatchQuery -> interpret(objectOrEmpty)
        is RangeQuery -> interpret(objectOrEmpty)
        is BoolQuery -> interpret(objectOrEmpty)
        is BoostingQuery -> interpret(objectOrEmpty)
        is ConstantScoreQuery -> interpret(objectOrEmpty)
        is DisMaxQuery -> interpret(objectOrEmpty)
        else -> json {}.success()
    }
}

fun TermQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field)
    if (field.value == null) return missingField(field::value)

    val termFieldOut = json {
        customProp?.let {
            it.first to it.second.json()
        } ?: field.name to json {
            prop(field::value) { it.json() }
            prop(field::boost) { it.json() }
        }
    }

    val termOut = parent { "term" to termFieldOut }
    return termOut.success()
}

fun MatchQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field)
    if (field.query == null) return missingField(field::query)

    val matchFieldOut = with(field) {
        json {
            prop(::query) {
                when (it) {
                    is AString -> it.value.json()
                    is ANumber -> it.value.json()
                    is ABoolean -> it.value.json()
                    is ADate -> it.value.toString().json()
                    else -> JsonNull
                }
            }
            prop(::analyzer) { it.json() }
            prop(::autoGeneratedSynonymsPhraseQuery) { it.json() }
            prop(::fuzziness) {
                when (it) {
                    is Fuzziness._0 -> 0.json()
                    is Fuzziness._1 -> 1.json()
                    is Fuzziness._2 -> 2.json()
                    is Fuzziness.Auto -> it.distances?.let { distances ->
                        val (low, high) = distances
                        "AUTO:[$low],[$high]".json()
                    } ?: "AUTO".json()
                }
            }
            prop(::maxExpansions) { it.json() }
            prop(::prefixLength) { it.json() }
            prop(::transpositions) { it.json() }
            propEnum(::fuzzyRewrite)
            prop(::lenient) { it.json() }
            propEnum(::operator)
            prop(::minimumShouldMatch) { it.interpret(this) }
            propEnum(::zeroTermsQuery)
        }
    }

    val matchOut = parent { "match" to json { field.name to matchFieldOut } }
    return matchOut.success()
}

fun RangeQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field)

    val rangeFieldOut = with(field) {
        json {
            fun <R, C : KCallable<R>> propFieldParam(callable: C) {
                prop(callable) {
                    when (it) {
                        is ANumber -> it.value.json()
                        is DateFormat -> it.lowerName().json()
                        else -> JsonNull
                    }
                }
            }
            propFieldParam(::gt)
            propFieldParam(::gte)
            propFieldParam(::lt)
            propFieldParam(::lte)

            propEnum(::format)
            propEnum(::relation)
            prop(::timeZone) { it.json() }
            prop(::boost) { it.json() }
        }
    }

    val rangeOut = parent { "range" to json { field.name to rangeFieldOut } }
    return rangeOut.success()
}

fun BoolQuery.interpret(parent: JsonObject): Evaluation {
    val propsOut = json {
        propWithAlt(::_must, ::must) { it.interpret() }
        propWithAlt(::_should, ::should) { it.interpret() }
        propWithAlt(::_filter, ::filter) { it.interpret() }
        propWithAlt(::_mustNot, ::mustNot) { it.interpret() }
        prop(::minimumShouldMatch) { it.interpret(this) }
        prop(::boost) { it.json() }
    }
    val boolOut = parent { "bool" to propsOut }
    return boolOut.validate()
}

fun BoostingQuery.interpret(parent: JsonObject): Evaluation {
    if (_positive == null) return missingField(::positive)
    if (_negative == null) return missingField(::negative)

    val propsOut = json {
        propWithAlt(::_positive, ::positive) { it.interpret() }
        propWithAlt(::_negative, ::negative) { it.interpret() }
        prop(::negativeBoost) { it.json() }
    }

    val boostingOut = parent { "boosting" to propsOut }
    return boostingOut.success()
}

fun ConstantScoreQuery.interpret(parent: JsonObject): Evaluation {
    if (_filter == null) return missingField(::filter)

    return parent {
        "constant_score" to json {
            propWithAlt(::_filter, ::filter) { it.interpret() }
            prop(::boost) { it.json() }
        }
    }.success()
}

fun DisMaxQuery.interpret(parent: JsonObject): Evaluation {
    if (_queries == null) return missingField(::queries)

    return parent {
        "dis_max" to json {
            propWithAlt(::_queries, ::queries) { it.interpret() }
            prop(::tieBreaker) { it.json() }
        }
    }.success()
}

fun MultiClause.interpret(): Evaluation {
    if (this.size == 1)
        return this[0].interpret()

    val result: JsonArray = jsonArray()

    for (clause in this) {
        val eval = clause.interpret()
        if (eval is Failed) return eval
        result.add(eval.value())
    }

    return result.success()
}

fun MinimumShouldMatch.interpret(parent: JsonObject): JsonValue {
    val msm: MinimumShouldMatch = this
    return with(parent) {
        fun interpretSimple(it: SimpleMSM): JsonValue = when (it) {
            is ANumber -> it.value.json()
            is Percent -> "${it.value}%".json()
            else -> JsonNull
        }
        fun interpretMin(it: MinimumShouldMatch): JsonValue = when (it) {
            is SimpleMSM -> interpretSimple(it)
            is Combination -> "${it.value}<${interpretSimple(it.simple)}".json()
            is Multiple -> array(it.values.map { i -> interpretMin(i) })
            else -> JsonNull
        }
        interpretMin(msm)
    }
}