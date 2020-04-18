package com.melvic.archia.interpreter

import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import kotlin.reflect.KCallable

fun String.toSnakeCase(): String {
    val snake = StringBuilder()

    for (char in this) {
        if (char.isUpperCase()) {
            if (!snake.isEmpty()) snake.append("_")
            snake.append(char.toLowerCase())
        } else snake.append(char)
    }

    return snake.toString()
}

fun <R> nameOf(callable: KCallable<R>): String = callable.name

fun <R> snakeCaseNameOf(callable: KCallable<R>): String = nameOf(callable).toSnakeCase()

inline fun <R> JsonObject.propEval(field: KCallable<R?>, f: (R) -> Evaluation) {
    field.call()?.let { snakeCaseNameOf(field) to f(it) }
}

inline fun <R> JsonObject.prop(field: KCallable<R?>, f: (R) -> JsonValue) {
    propEval(field) { f(it).success() }
}

inline fun <R> JsonObject.propWithAlt(
    field: KCallable<R?>,
    altField: KCallable<Unit>,
    f: (R) -> Evaluation
) {
    field.call()?.let { snakeCaseNameOf(altField) to f(it) }
}

fun <E : Enum<E>> JsonObject.propEnum(callable: KCallable<E?>) {
    this.prop(callable) { text(it.lowerName()) }
}

fun <E : Enum<E>> E.lowerName(): String = this.name.toLowerCase()
