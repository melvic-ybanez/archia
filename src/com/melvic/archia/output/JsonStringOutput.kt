package com.melvic.archia.output

object JsonStringOutput : Transformer<String> {
    const val SEPARATOR = ","

    override fun jsonNull(json: JsonValue.JsonNull): String = "null"

    override fun jsonBoolean(json: JsonValue.JsonBoolean): String =
        json.value.toString()

    override fun jsonNumber(json: JsonValue.JsonNumber): String =
        json.value.toString()

    override fun jsonString(json: JsonValue.JsonString): String = fromString(json.value)

    override fun jsonObject(json: JsonValue.JsonObject): String {
        val stringEntries = json.entries.map { (key, value) ->
            "${fromString(key)}: ${transform(value)}"
        }.joinToString(SEPARATOR)
        return "{$stringEntries}"
    }

    override fun jsonArray(json: JsonValue.JsonArray): String {
        val items = json.items.joinToString(SEPARATOR, transform = ::transform)
        return "[$items]"
    }

    private fun fromString(str: String) = """"$str""""
}