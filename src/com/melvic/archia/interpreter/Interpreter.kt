package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.compound.BoolQuery
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.output.JsonNull
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import kotlin.reflect.KCallable

typealias Evaluation = Result<JsonValue>

fun Query.interpret(): Evaluation {
    fun interpret(query: Clause, parent: JsonValue): Evaluation {
        val objectOrEmpty = if (parent is JsonObject) parent else json {}

        return when (query) {
            is TermQuery -> query.interpret(objectOrEmpty)
            is MatchQuery -> query.interpret(objectOrEmpty)
            is RangeQuery -> query.interpret(objectOrEmpty)
            else -> json {}.success()
        }
    }

    val output = this.queryClause?.let {
        interpret(it, json {})
    } ?: missingField(this::query).fail()

    return when (output) {
        is Failed -> output
        is Success<*> -> json {
            "query" to output.value()
        }.success()
    }
}

fun TermQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field).fail()
    if (field.value == null) return missingField(field::value).fail()

    val termFieldOut = json {
        prop(field::value) { text(it) }
        prop(field::boost) { num(it) }
    }

    val termOut = parent { "term" to json { field.name to termFieldOut } }
    return termOut.success()
}

fun MatchQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field).fail()
    if (field.query == null) return missingField(field::query).fail()

    val matchFieldOut = with(field) {
        json {
            prop(::query) {
                when (it) {
                    is AString -> text(it.value)
                    is ANumber -> num(it.value)
                    is ABoolean -> bool(it.value)
                    is ADate -> text(it.value.toString())
                    else -> JsonNull
                }
            }
            prop(::analyzer) { text(it) }
            prop(::autoGeneratedSynonymsPhraseQuery) { bool(it) }
            prop(::fuzziness) {
                when (it) {
                    is Fuzziness._0 -> num(0)
                    is Fuzziness._1 -> num(1)
                    is Fuzziness._2 -> num(2)
                    is Fuzziness.Auto -> it.distances?.let { distances ->
                        val (low, high) = distances
                        text("AUTO:[$low],[$high]")
                    } ?: text("AUTO")
                }
            }
            prop(::maxExpansions) { num(it) }
            prop(::prefixLength) { num(it) }
            prop(::transpositions) { bool(it) }
            prop(::fuzzyRewrite)
            prop(::lenient) { bool(it) }
            prop(::operator)
            prop(::minimumShouldMatch) min@ {
                fun interpretSimple(it: SimpleMSM): JsonValue = when (it) {
                    is ANumber -> num(it.value)
                    is Percent -> text("${it.value}%")
                    else -> JsonNull
                }
                fun interpretMin(it: MinimumShouldMatch): JsonValue = when (it) {
                    is SimpleMSM -> interpretSimple(it)
                    is Combination -> text("${it.value}<${interpretSimple(it.simple)}")
                    is Multiple -> array(it.values.map { i -> interpretMin(i) })
                    else -> JsonNull
                }
                interpretMin(it)
            }
            prop(::zeroTermsQuery)
        }
    }

    val matchOut = parent { "match" to json { field.name to matchFieldOut } }
    return matchOut.success()
}

fun RangeQuery.interpret(parent: JsonObject): Evaluation {
    val field = this.field ?: return missingField(this::field).fail()

    val rangeFieldOut = with(field) {
        json {
            fun <R, C : KCallable<R>>propFieldParam(callable: C) {
                prop(callable) {
                    when (it) {
                        is ANumber -> num(it.value)
                        is DateFormat -> text(it.lowerName())
                        else -> JsonNull
                    }
                }
            }
            propFieldParam(::gt)
            propFieldParam(::gte)
            propFieldParam(::lt)
            propFieldParam(::lte)

            prop(::format)
            prop(::relation)
            prop(::timeZone) { text(it) }
            prop(::boost) { num(it) }
        }
    }

    val rangeOut = parent { "range" to json { field.name to rangeFieldOut }}
    return rangeOut.success()
}
