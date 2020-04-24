package com.melvic.archia.interpreter.fulltext

import com.melvic.archia.ast.fulltext.*
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import com.melvic.archia.require

/**
 * Interprets an intervals query
 * @param parent the json object that will contain the interpreted
 * form of the given intervals query
 */
fun IntervalsQuery.interpret(parent: JsonObject): Evaluation {
    return interpret(parent) {
        json {
            // TODO implement interpreter
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
        // TODO: filter rule
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
    return require(::intervals) {
        propNum(::maxGaps)
        propBool(::ordered)
        // TODO: filter rule
    }
}