package com.melvic.archia.interpreter.fulltext

import com.melvic.archia.ast.Param
import com.melvic.archia.ast.fulltext.*
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.*
import com.melvic.archia.require
import com.melvic.archia.validate
import org.omg.CORBA.DynAnyPackage.Invalid

/**
 * Interprets intervals query
 * @param parent the json object that will contain the interpreted
 * form of the given intervals query
 */
fun IntervalsQuery.interpret(parent: JsonObject): Evaluation {
    return withField(parent) {
        json {
            rule?.interpret(this)
        }
    }
}

fun Param<IntervalRule>.interpret(parent: JsonObject): JsonObject {
    val (name, clause) = this
    return parent {
        name to when (clause) {
            is MatchRule -> clause.interpret()
            is PrefixRule -> clause.interpret()
            is WildCardRule -> clause.interpret()
            is FuzzyRule -> clause.interpret()
            is AllOfRule -> clause.interpret()
            is AnyOfRule -> clause.interpret()
            else -> InvalidValue(name, clause).fail()
        }
    }
}

fun WithAnalyzer.interpretAnalyzer(parent: JsonObject): JsonObject {
    return parent {
        propStr(::analyzer)
        propStr(::useField)
    }
}

fun MatchRule.interpret(): Evaluation {
    return require(::query) {
        propNum(::maxGaps)
        propBool(::ordered)
        interpretAnalyzer(this)
        propFunc(::_filter) { it.interpret(json {}) }
    }
}

fun PrefixRule.interpret(): Evaluation {
    return require(::prefix) {
        interpretAnalyzer(this)
    }
}

fun WildCardRule.interpret(): Evaluation {
    return require(::pattern) {
        interpretAnalyzer(this)
    }
}

fun FuzzyRule.interpret(): Evaluation {
    return require(::term) {
        propStr(::prefixLength)
        propBool(::transpositions)
        prop(::fuzziness) { it.interpret(this) }
        interpretAnalyzer(this)
    }
}

fun AllOfRule.interpret(): Evaluation {
    return interpretIntervalOptions {
        propNum(::maxGaps)
        propBool(::ordered)
    }
}

fun AnyOfRule.interpret(): Evaluation {
    return interpretIntervalOptions {}
}

fun IntervalOptions.interpretIntervalOptions(body: JsonObject.() -> Unit): Evaluation {
    return require(::intervals, _intervals) {
        body()
        propFunc(::_intervals) prop@ {
            val intervalRules = jsonArray()
            for (interval in it) {
                val eval = interval.interpret(json {}).validate()
                if (eval is Failed) return@prop eval
                intervalRules.add(eval.value())
            }
            intervalRules.success()
        }
        propFunc(::_filter) { it.interpret(json {}) }
    }
}

fun FilterRule.interpret(parent: JsonObject): Evaluation =
    query?.let {
        val (name, clause) = it
        parent { name to clause.interpret(json {})}.validate()
    } ?: json {}.success()