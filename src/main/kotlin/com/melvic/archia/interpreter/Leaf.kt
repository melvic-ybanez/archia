package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.output.JsonNull
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.json
import kotlin.reflect.KCallable

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

    val termOut = parent { esName() to termFieldOut }
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

    val matchOut = parent { esName() to json { field.name to matchFieldOut } }
    return matchOut.success()
}

fun MatchAllQuery.interpret(parent: JsonObject): Evaluation {
    return parent {
        esName() to json { prop(::boost) { it.json() } }
    }.success()
}

fun MatchNoneQuery.interpret(parent: JsonObject): Evaluation {
    return parent { esName() to json {} }.success()
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

    val rangeOut = parent { esName() to json { field.name to rangeFieldOut } }
    return rangeOut.success()
}