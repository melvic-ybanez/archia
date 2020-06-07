package com.melvic.archia.ast.compound

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.geo.GeoObject
import com.melvic.archia.script.Script
import kotlin.reflect.KCallable

class FunctionScoreQuery : FunctionClause() {
    private var functions: List<FunctionClause> by parameters

    var query: Clause by parameters
    var boost: String by parameters
    var maxBoost: Int by parameters
    var scoreMode: ScoreMode by parameters
    var boostMode: BoostMode by parameters
    var minScore: Int by parameters

    fun query(init: Init<ClauseBuilder>) {
        query = ClauseBuilder().apply(init).clause
    }

    fun functions(vararg init: Init<FunctionClause>) {
        functions = init.map { FunctionClause().apply(it) }
    }
}

open class FunctionClause: Clause(), ParamHelper, WithDate, BuilderHelper {
    private var scoreFunction: Param<ScoreFunction> by parameters
    var filter: Clause by parameters
    var weight: Number by parameters

    private inline fun <reified R : ScoreFunction> save(init: Init<R>, field: KCallable<Unit>) {
        setProp(init) { scoreFunction = param(field, it) }
    }

    override val topLevel: Boolean = false

    fun filter(init: Init<ClauseBuilder>) {
        setClause(init) { filter = it }
    }

    fun scriptScore(init: Init<ScriptScore>) {
        save(init, ::scriptScore)
    }

    fun randomScore(init: Init<RandomScore>) {
        save(init, ::randomScore)
    }

    fun fieldValueFactor(init: Init<FieldValueFactor>) {
        save(init, ::fieldValueFactor)
    }

    fun gauss(init: Init<DecayFunction>) {
        save(init, ::gauss)
    }

    fun exp(init: Init<DecayFunction>) {
        save(init, ::exp)
    }

    fun linear(init: Init<DecayFunction>) {
        save(init, ::linear)
    }
}

open class ScoreFunction : TreeNode()

class RandomScore : ScoreFunction() {
    var seed: Number by parameters
    var field: String by parameters
}

class ScriptScore : ScoreFunction() {
    var script: Script by parameters

    fun script(init: Init<Script>) {
        setProp(init) { script = it }
    }
}

class FieldValueFactor: ScoreFunction() {
    var field: String by parameters
    var factor: Double by parameters
    var modifier: Modifier by parameters
    var missing: Int by parameters
}

class DecayFunction: ScoreFunction() {
    var field: DecayFunctionField by parameters
    var multiValueMode: MultiValueMode by parameters

    operator fun String.invoke(init: Init<DecayFunctionField>) {
        field = DecayFunctionField(this).apply(init)
    }
}

class DecayFunctionField(name: String): Field(name), WithText {
    var origin: DecayFieldType by parameters
    var scale: String by parameters
    var offset: String by parameters
    var decay: Double by parameters

    fun origin(init: Init<GeoObject>) {
        setProp(init) { origin = it }
    }
}

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