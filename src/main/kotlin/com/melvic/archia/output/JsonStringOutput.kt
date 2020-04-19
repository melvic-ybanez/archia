package main.kotlin.com.melvic.archia.output

object JsonStringOutput : Transformer<String> {
    const val SEPARATOR = ","

    override fun jsonNull(json: JsonNull): String = "null"

    override fun jsonBoolean(json: JsonBoolean): String =
        json.value.toString()

    override fun jsonNumber(json: JsonNumber): String =
        json.value.toString()

    override fun jsonString(json: JsonString): String = fromString(
        json.value
    )

    override fun jsonObject(json: JsonObject): String {
        val stringEntries = json.entries.map { (key, value) ->
            "${fromString(key)}: ${transform(value)}"
        }.joinToString(SEPARATOR)
        return "{$stringEntries}"
    }

    override fun jsonArray(json: JsonArray): String {
        val items = json.items.joinToString(SEPARATOR, transform = ::transform)
        return "[$items]"
    }

    private fun fromString(str: String) = """"$str""""
}