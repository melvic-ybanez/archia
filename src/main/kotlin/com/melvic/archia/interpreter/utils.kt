package com.melvic.archia.interpreter

import com.melvic.archia.ast.Clause
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonString
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

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

fun <C : Any> snakeCaseNameOf(clazz: KClass<C>): String =
    clazz.simpleName?.toSnakeCase() ?: "unknown_type"

inline fun <R> JsonObject.propEval(field: KCallable<R?>, f: (R) -> Evaluation) {
    field.call()?.let { snakeCaseNameOf(field) to f(it) }
}

inline fun <R> JsonObject.prop(field: KCallable<R?>, f: (R) -> JsonValue) {
    propEval(field) { f(it).success() }
}

fun JsonObject.propStr(field: KCallable<String?>) {
    this { prop(field) { it.json() } }
}

fun JsonObject.propNum(field: KCallable<Number?>) {
    this { prop(field) { it.json() } }
}

fun JsonObject.propBool(field: KCallable<Boolean>) {
    this { prop(field) { it.json() } }
}

inline fun <R> JsonObject.propFunc(
    field: KCallable<R?>,
    altField: KCallable<Unit>,
    f: (R) -> Evaluation
) {
    field.call()?.let { snakeCaseNameOf(altField) to f(it) }
}

fun <E : Enum<E>> JsonObject.propEnum(callable: KCallable<E?>) {
    this.prop(callable) { it.lowerName().json() }
}

fun <E : Enum<E>> E.lowerName(): String = this.name.toLowerCase()
