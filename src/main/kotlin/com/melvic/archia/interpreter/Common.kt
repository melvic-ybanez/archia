package com.melvic.archia.interpreter

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Fuzziness
import com.melvic.archia.ast.WithField
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
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

fun Fuzziness.interpret(parent: JsonObject): JsonValue {
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