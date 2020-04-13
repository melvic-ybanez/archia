package com.melvic.archia.output

sealed class JsonValue {
    data class JsonString(val value: String) : JsonValue()
    data class JsonNumber(val value: Number) : JsonValue()
    data class JsonBoolean(val value: Boolean) : JsonValue()
    object JsonNull : JsonValue()
    class JsonObject(val entries: Map<String, JsonValue>) : JsonValue()
    class JsonArray(val items: List<JsonValue>) : JsonValue()
}

fun <T, J : JsonValue> J.transform(transformer: Transformer<T>): T {
    return transformer.transform(this)
}