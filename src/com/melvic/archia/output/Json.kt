package com.melvic.archia.output

import com.melvic.archia.ast.Init
import com.melvic.archia.interpreter.ErrorCode
import com.melvic.archia.interpreter.Evaluation
import com.melvic.archia.interpreter.Failed
import com.melvic.archia.interpreter.validate

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

data class JsonArray(val items: MutableList<JsonValue>) : JsonValue(), Compound<JsonArray> {
    override fun instance() = this

    fun add(json: JsonValue) {
        items.add(json)
    }
}

data class JsonObject(
    val entries: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue(), Compound<JsonObject> {
    val errors: MutableList<ErrorCode> = mutableListOf()

    infix fun String.to(json: JsonValue) {
        if (json is JsonObject) {
            this to json.validate()
        } else {
            entries[this] = json
        }
    }

    infix fun String.to(eval: Evaluation) {
        if (eval is Failed) errors.addAll(eval.errors)
        else entries[this] = eval.value()
    }

    fun array(vararg value: JsonValue) = jsonArray(*value)

    fun array(values: List<JsonValue>) = JsonArray(values.toMutableList())

    fun Number.json() = JsonNumber(this)
    fun String.json() = JsonString(this)
    fun Boolean.json() = JsonBoolean(this)

    override fun instance() = this
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


