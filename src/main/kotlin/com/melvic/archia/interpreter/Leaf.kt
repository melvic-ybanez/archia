@file:Suppress("MoveSuspiciousCallableReferenceIntoParentheses")

package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.*
import com.melvic.archia.output.*
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