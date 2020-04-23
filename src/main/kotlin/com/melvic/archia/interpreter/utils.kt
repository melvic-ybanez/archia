package com.melvic.archia.interpreter

import com.melvic.archia.ast.Param
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
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

/**
 * Builds a property from a field with an alternative.
 * @param field the backing field
 * @param altField alternative field
 * @param f maps the output to an Evaluation. It only works if the output
 * is not a null
 */
inline fun <R> JsonObject.propWithAlt(
    field: KCallable<R?>,
    altField: KCallable<Unit>,
    f: (R) -> Evaluation
) {
    propWithAlt(field, snakeCaseNameOf(altField), f)
}

/**
 * Builds a property with an alternative name
 */
inline fun <R> JsonObject.propWithAlt(
    field: KCallable<R?>,
    altName: String,
    f: (R) -> Evaluation
) {
    field.call()?.let { altName to f(it) }
}

/**
 * Builds a function property with an autogenerated alternative name.
 * The alternative name is based on the name of the given field without
 * the leading underscore. If the name doesn't have an underscore, the
 * function doesn't do anything.
 */
inline fun <R> JsonObject.propFunc(field: KCallable<R?>, f: (R) -> Evaluation) {
    if (!field.name.startsWith("_")) return
    val altName = field.name.substring(1).toSnakeCase()
    propWithAlt(field, altName, f)
}

fun <E : Enum<E>> JsonObject.propEnum(callable: KCallable<E?>) {
    this.prop(callable) { it.lowerName().json() }
}

fun <A> JsonObject.propParam(param: Param<A>?, f: (A) -> Evaluation) {
    param?.let { it.first.toSnakeCase() to f(it.second) }
}

fun <E : Enum<E>> E.lowerName(): String = this.name.toLowerCase()
