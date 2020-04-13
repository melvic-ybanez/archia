package com.melvic.archia

sealed class JsonValue {
    data class JsonString(val str: String) : JsonValue()
    data class JsonNumber(val num: Number) : JsonValue()
    data class JsonBoolean(val bool: Boolean) : JsonValue()
    object JsonNull : JsonValue()
    class JsonObject(val entries: Map<String, JsonValue>) : JsonValue()
    class JsonArray(val items: List<JsonValue>) : JsonValue()
}