package com.melvic.archia.ast.compound

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Init
import com.melvic.archia.script.Script

data class FunctionScoreQuery(
    var query: Clause? = null,
    var boost: Boost? = null,
    var _functions: MutableList<Function>? = null,
    var maxBoost: Boost? = null,
    var scoreMode: ScoreMode? = null,
    var boostMode: BoostMode? = null,
    var minScore: Float? = null
) : Function() {
    fun function(init: Init<Function>) {
        val newFunction = Function().also(init)
        _functions?.add(newFunction) ?: run {
            _functions = mutableListOf(newFunction)
        }
    }
}

open class Function(
    var filter: Clause? = null,
    // score functions (plus decay functions below)
    var _scriptScore: Script? = null,
    var weight: Number? = null,
    var _randomScore: RandomScore? = null,
    var _fieldValueFactor: FieldValueFactor? = null,

    // decay functions
    var _gauss: DecayFunction? = null,
    var _exp: DecayFunction? = null,
    var _linear: DecayFunction? = null
) : Clause {
    fun scriptScore(init: Init<Script>) {
        _scriptScore = Script().apply(init)
    }

    fun randomScore(init: Init<RandomScore>) {
        _randomScore = RandomScore().apply(init)
    }

    fun fieldValueFactor(init: Init<FieldValueFactor>) {
        _fieldValueFactor = FieldValueFactor().apply(init)
    }

    fun gauss(init: Init<DecayFunction>) {
        _gauss = DecayFunction().apply(init)
    }

    fun exp(init: Init<DecayFunction>) {
        _exp = DecayFunction().apply(init)
    }

    fun linear(init: Init<DecayFunction>) {
        _linear = DecayFunction().apply(init)
    }
}

data class RandomScore(var seed: Number? = null, var field: String? = null)

data class FieldValueFactor(
    var field: String? = null,
    var factor: Float? = null,
    var modifier: Modifier? = null,
    var missing: Float? = null
)

data class DecayFunction(
    var field: DecayFunctionField? = null,
    var multiValueMode: MultiValueMode? = null
) {
    operator fun String.invoke(init: Init<DecayFunctionField>) {
        field = DecayFunctionField().apply(init)
    }
}

data class DecayFunctionField(
    var name: String? = null,
    var origin: String? = null,
    var scale: String? = null,
    var offset: String? = null,
    var decay: Float? = null
)

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