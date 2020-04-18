package com.melvic.archia.output

import com.melvic.archia.ast.Init
import com.melvic.archia.interpreter.ErrorCode
import com.melvic.archia.interpreter.Evaluation
import com.melvic.archia.interpreter.Failed

/**
 * Javascript Object Notation.
 *
 * DSL is provided for the ease of use and testing.
 */
sealed class JsonValue

object JsonNull : JsonValue()

data class JsonString(val value: String) : JsonValue()
data class JsonNumber(val value: Number) : JsonValue()
data class JsonBoolean(val value: Boolean) : JsonValue()

data class JsonArray(val items: MutableList<JsonValue>) : JsonValue(), JsonHelper, Compound<JsonArray> {
    override fun instance() = this

    fun add(json: JsonValue) {
        items.add(json)
    }
}

data class JsonObject(
    val entries: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue(), JsonHelper, Compound<JsonObject> {
    val errors: MutableList<ErrorCode> = mutableListOf()

    infix fun <J : JsonValue> String.to(json: J) {
        entries[this] = json
    }

    infix fun String.to(eval: Evaluation) {
        if (eval is Failed) errors.add(eval.code)
        this to eval.value()
    }

    fun array(vararg value: JsonValue) = jsonArray(*value)

    fun array(values: List<JsonValue>) = JsonArray(values.toMutableList())

    override fun instance() = this
}

interface JsonHelper {
    fun num(value: Number) = JsonNumber(value)
    fun text(value: String) = JsonString(value)
    fun bool(value: Boolean) = JsonBoolean(value)
}

interface Compound<C> {
    fun instance(): C

    operator fun invoke(init: Init<C>): C = instance().apply(init)
}

fun <T, J : JsonValue> J.transform(transformer: Transformer<T>): T {
    return transformer.transform(this)
}

fun json(init: Init<JsonObject>) = JsonObject().apply(init)

fun jsonArray(vararg value: JsonValue) = JsonArray(mutableListOf(*value))


