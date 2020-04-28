package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.fulltext.CommonTermsField
import com.melvic.archia.output.*
import com.melvic.archia.validate
import kotlin.reflect.KCallable

fun MinimumShouldMatch.interpret(parent: JsonObject = json {}): JsonValue {
    val msm: MinimumShouldMatch = this
    return with(parent) {
        fun interpretSimple(it: SimpleMSM): JsonValue = when (it) {
            is ANumber -> it.value.json()
            is Percent -> "${it.value}%".json()
            else -> JsonNull
        }
        fun interpretMin(it: MinimumShouldMatch): JsonValue = when (it) {
            is SimpleMSM -> interpretSimple(it)
            is Combination -> "${it.value}<${interpretSimple(it.simple)}".json()
            is Multiple -> array(it.values.map { i -> interpretMin(i) })

            // extended form of MSM
            is CommonTermsField.MinimumShouldMatchWithFreq -> json {
                prop(it::lowFreq) { it.interpret(parent) }
                prop(it::highFreq) { it.interpret(parent) }
            }

            else -> JsonNull
        }
        interpretMin(msm)
    }
}

fun Fuzziness.interpret(parent: JsonObject = json {}): JsonValue {
    return parent {
        when (this@interpret) {
            is Fuzziness._0 -> 0.json()
            is Fuzziness._1 -> 1.json()
            is Fuzziness._2 -> 2.json()
            is Fuzziness.Auto -> this@interpret.distances?.let { distances ->
                val (low, high) = distances
                "AUTO:[$low],[$high]".json()
            } ?: "AUTO".json()
        }
    }
}

fun Geo.interpret(): JsonValue {
    return when (this) {
        is GeoObject -> with(this) {
            json { propNum(::lat); propNum(::long) }
        }
        is GeoString -> JsonString("${this.lat},${this.long}")
        is GeoHash -> JsonString(this.hash)
        is GeoArray -> jsonArray(JsonNumber(lat), JsonNumber(long))
        is GeoWktPoint -> JsonString("POINT (${this.lat} )")
    }
}