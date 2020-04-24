package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import com.melvic.archia.script.Script
import kotlin.reflect.KCallable

data class FunctionScoreQuery(
    var _query: Clause? = null,
    var boost: String? = null,
    var _functions: MutableList<FunctionClause>? = null,
    var maxBoost: Int? = null,
    var scoreMode: ScoreMode? = null,
    var boostMode: BoostMode? = null,
    var minScore: Int? = null
) : FunctionClause() {
    fun query(init: Init<ClauseBuilder>) {
        _query = ClauseBuilder().apply(init).clause
    }

    fun function(init: Init<FunctionClause>) {
        val newFunction = FunctionClause().also(init)
        _functions?.add(newFunction) ?: run {
            _functions = mutableListOf(newFunction)
        }
    }
}

open class FunctionClause(
    var _filter: Clause? = null,
    var scoreFunction: Param<ScoreFunction>? = null,
    var weight: Number? = null
) : Clause, ParamHelper, WithDate, BuilderHelper {
    fun saveFunction(callable: KCallable<Unit>, sf: ScoreFunction) {
        scoreFunction = param(callable, sf)
    }

    fun filter(init: Init<ClauseBuilder>) {
        setClause(init) { _filter = it }
    }

    fun scriptScore(init: Init<ScriptScore>) {
        setProp(init) { saveFunction(::scriptScore, it) }
    }

    fun randomScore(init: Init<RandomScore>) {
        setProp(init) { saveFunction(::randomScore, it) }
    }

    fun fieldValueFactor(init: Init<FieldValueFactor>) {
        setProp(init) { saveFunction(::fieldValueFactor, it) }
    }

    fun gauss(init: Init<DecayFunction>) {
        setProp(init) { saveFunction(::gauss, it) }
    }

    fun exp(init: Init<DecayFunction>) {
        setProp(init) { saveFunction(::exp, it) }
    }

    fun linear(init: Init<DecayFunction>) {
        setProp(init) { saveFunction(::linear, it) }
    }
}

interface ScoreFunction

data class RandomScore(var seed: Number? = null, var field: String? = null) : ScoreFunction

data class ScriptScore(var _script: Script? = null) : ScoreFunction {
    fun script(init: Init<Script>) {
        setProp(init) { _script = it }
    }
}

data class FieldValueFactor(
    var field: String? = null,
    var factor: Double? = null,
    var modifier: Modifier? = null,
    var missing: Int? = null
) : ScoreFunction

data class DecayFunction(
    var field: DecayFunctionField? = null,
    var multiValueMode: MultiValueMode? = null
) : ScoreFunction {
    operator fun String.invoke(init: Init<DecayFunctionField>) {
        field = DecayFunctionField(this).apply(init)
    }
}

data class DecayFunctionField(
    val name: String,
    var origin: DecayFieldType? = null,
    var scale: String? = null,
    var offset: String? = null,
    var decay: Double? = null
) : WithNum

enum class Modifier {
    NONE, LOG, LOG1P, LOG2P, LN, LN1P, LN2P, SQUARE, SQRT, RECIPROCAL
}

enum class MultiValueMode {
    MIN, MAX, AVG, SUM
}

enum class ScoreMode {
    MULTIPLY, SUM, AVG, FIRST, MAX, MIN
}

enum class BoostMode {
    MULTIPLY, REPLACE, SUM, AVG, MAX, MIN
}