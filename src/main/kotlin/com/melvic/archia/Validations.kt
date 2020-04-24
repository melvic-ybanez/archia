package com.melvic.archia

import com.melvic.archia.interpreter.Evaluation
import com.melvic.archia.interpreter.Failed
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.success
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.json
import kotlin.reflect.KCallable

/**
 * Checks if a JSON object contains errors. If so, it returns
 * a Failed object with the errors. Otherwise, a Success object
 * is returned.
 */
fun JsonObject.validate(): Evaluation {
    return if (this.errors.isEmpty()) {
        this.success()
    } else Failed(this.errors)
}

/**
 * Validates a required field.
 *
 * Note: Use this only if you want to short-circuit the operation, and
 * return the error immediately, instead of aggregating all the possible
 * errors.
 * @param field the required field
 * @param f function to execute when the required fields are provided
 */
fun <F> require(field: KCallable<F?>, f: JsonObject.() -> Unit): Evaluation {
    if (field.call() == null) return missingField(field.name)
    return json {}.apply(f).validate()
}