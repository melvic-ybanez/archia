package main.kotlin.com.melvic.archia.output

interface Transformer<T> {
    fun <J : JsonValue> transform(json: J): T =
        when (json) {
            is JsonNull -> jsonNull(json)
            is JsonBoolean -> jsonBoolean(json)
            is JsonString -> jsonString(json)
            is JsonNumber -> jsonNumber(json)
            is JsonObject -> jsonObject(json)
            is JsonArray -> jsonArray(json)
            else -> throw Exception("Invalid Json case")
        }

    fun jsonArray(json: JsonArray): T

    fun jsonNull(json: JsonNull): T

    fun jsonBoolean(json: JsonBoolean): T

    fun jsonNumber(json: JsonNumber): T

    fun jsonString(json: JsonString): T

    fun jsonObject(json: JsonObject): T
}