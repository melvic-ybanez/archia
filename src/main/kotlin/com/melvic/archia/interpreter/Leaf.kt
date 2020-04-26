@file:Suppress("MoveSuspiciousCallableReferenceIntoParentheses")

package com.melvic.archia.interpreter

import com.melvic.archia.ast.ANumber
import com.melvic.archia.ast.DateFormat
import com.melvic.archia.ast.leaf.RangeQuery
import com.melvic.archia.ast.leaf.TermQuery
import com.melvic.archia.output.JsonNull
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.json
import kotlin.reflect.KCallable

fun TermQuery.interpret(parent: JsonObject): Evaluation {
    val namedPropOut = namedProp?.let {
        json { it.first to it.second.json() }
    }

    return withField(parent, namedProp = namedPropOut, required = { ::value }) {
        json {
            prop(::value) { it.json() }
            prop(::boost) { it.json() }
        }
    }
}

fun RangeQuery.interpret(parent: JsonObject): Evaluation {
    return withField(parent) {
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
}