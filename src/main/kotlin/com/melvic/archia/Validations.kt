package com.melvic.archia

import com.melvic.archia.ast.TreeNode
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
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

fun JsonValue.validate(): Evaluation {
    return if (this is JsonObject) this.validate() else this.success()
}

fun JsonObject.validateRequiredParams(tree: TreeNode) {
    for (fieldName in tree.requiredParams) {
        if (!tree.parameters.containsKey(fieldName.name)) {
            error(missingFieldCode(fieldName))
        }
    }
}

/**
 * Validates a required field.
 *
 * Note: Use this only if you want to short-circuit the operation, and
 * return the error immediately, instead of aggregating all the possible
 * errors.
 * @param field the required field
 * @param out the result of calling the field. If you don't want to provide
 * this, use the other version of this overloaded function.
 * @param f function to execute when the required fields are provided
 */
fun <F> require(field: KCallable<F?>, out: F?, f: JsonObject.() -> Unit): Evaluation {
    val fieldValue = out?: return missingField(field.name)
    val fieldName = field.esNameFormat()

    return json {
        // If the field is a JSON primitive value, register it as
        // one of the props
        fieldValue.let {
            when (it) {
                is Number -> fieldName to it.json()
                is String -> fieldName to it.json()
                is Boolean -> fieldName to it.json()
            }
        }
    }.apply(f).validate()
}

fun <F> require(field: KCallable<F?>, f: JsonObject.() -> Unit): Evaluation {
    return require(field, field.call(), f)
}