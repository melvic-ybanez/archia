package com.melvic.archia.interpreter

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Fuzziness
import com.melvic.archia.ast.WithField
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import com.melvic.archia.validate

/**
 * Interprets a clause with a field, if the field exists. If the field is null,
 * the function will yield a missing field error.
 * @param build builder for the field. It only gets executed if the field exists.
 */
fun <F : Field> WithField<F>.interpret(parent: JsonObject, build: F.() -> JsonValue): Evaluation {
    val field = this.field ?: return missingField(this::field)
    val out = parent { esName() to json { field.name to build(field) } }
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