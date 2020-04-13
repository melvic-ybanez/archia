package com.melvic.archia.output

import com.melvic.archia.Init

/**
 * Javascript Object Notation.
 *
 * DSL is provided for the ease of use and testing.
 */
sealed class JsonValue {
    object JsonNull : JsonValue()
    data class JsonString(val value: String) : JsonValue()
    data class JsonNumber(val value: Number) : JsonValue()
    data class JsonBoolean(val value: Boolean) : JsonValue()

    data class JsonArray(val items: List<JsonValue>) : JsonValue(), JsonHelper

    data class JsonObject(
        var entries: MutableMap<String, JsonValue> = mutableMapOf()
    ) : JsonValue(), JsonHelper {
        infix fun <J : JsonValue> String.to(json: J) {
            entries[this] = json
        }

        fun array(vararg value: JsonValue) = json(*value)
    }

    interface JsonHelper {
        fun num(value: Number) = JsonNumber(value)
        fun str(value: String) = JsonString(value)
        fun bool(value: Boolean) = JsonBoolean(value)
    }
}

fun <T, J : JsonValue> J.transform(transformer: Transformer<T>): T {
    return transformer.transform(this)
}

fun json(init: Init<JsonValue.JsonObject>) = JsonValue.JsonObject().apply(init)

fun json(vararg value: JsonValue) =JsonValue.JsonArray(listOf(*value))
