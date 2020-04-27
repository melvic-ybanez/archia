package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.fulltext.CommonTermsField
import com.melvic.archia.output.*
import com.melvic.archia.validate
import kotlin.reflect.KCallable

/**
 * Interprets a clause with a field, if the field exists. If the field is null,
 * the function will yield a missing field error.
 * @param build builder for the field. It only gets executed if the field exists.
 */
fun <F : Field> WithField<F>.withField(
    parent: JsonObject,
    namedProp: JsonObject? = null,
    required: (F.() -> KCallable<Any?>)? = null,
    build: F.() -> JsonValue
): Evaluation {
    val field = this.field ?: return missingField(this::field)

    // Simplified forms don't have extra field name keys
    val fieldOut = namedProp ?: json {
        fun addField() = field.name to build(field)

        if (required == null) {
            // If there is no required field, proceed as-is.
            addField()
        } else {
            // Otherwise, report a missing field error
            val requiredField = field.required()
            val requiredFieldOut = requiredField.call()
            if (requiredFieldOut == null) {
                error(missingFieldCode(requiredField))
            } else addField()
        }
    }

    val out = parent { esName() to fieldOut }
    return out.validate()
}

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