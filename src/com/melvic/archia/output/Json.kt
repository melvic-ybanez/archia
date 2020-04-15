package com.melvic.archia.output

import com.melvic.archia.Init
import com.melvic.archia.interpreter.snakeCaseNameOf
import kotlin.reflect.KCallable

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

data class JsonArray(val items: List<JsonValue>) : JsonValue(), JsonHelper, Compound<JsonArray> {
    override fun instance() = this
}

data class JsonObject(
    var entries: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue(), JsonHelper, Compound<JsonObject> {
    infix fun <J : JsonValue> String.to(json: J) {
        entries[this] = json
    }

    fun array(vararg value: JsonValue) = json(*value)

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

fun json(vararg value: JsonValue) = JsonArray(listOf(*value))


