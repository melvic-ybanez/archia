package com.melvic.archia.interpreter

import com.melvic.archia.ast.Param
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.jsonArray
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * Yields the snake case form of a given string.
 * This is used to format the names of the class and callables
 * to match the elasticsearch field name format.
 */
fun String.toSnakeCase(): String {
    val snake = StringBuilder()

    for (char in this) {
        if (char.isUpperCase()) {
            if (snake.isNotEmpty()) snake.append("_")
            snake.append(char.toLowerCase())
        } else snake.append(char)
    }

    return snake.toString()
}

/**
 * Yields the callable's name, formatted based on the
 * elasticsearch field name format.
 */
fun <R> KCallable<R>.esNameFormat(): String {
    // Currently, callable names assumed to be in camel case format.
    // This might change in the future
    return this.name.toSnakeCase()
}

/**
 * Yields the class name, formatted based on the
 * elasticsearch field name format.
 */
fun <C : Any> KClass<C>.esNameFormat(): String {
    // Currently, class names assumed to be in camel case format.
    // This might change in the future
    return simpleName?.toSnakeCase() ?: "unknown_type"
}

/**
 * Evaluates a property. Add it to the JSON object if successful.
 * Otherwise, append the errors to the JSON's error list.
 */
inline fun <R> JsonObject.propEval(field: KCallable<R?>, f: (R) -> Evaluation) {
    field.call()?.let { field.esNameFormat() to f(it) }
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

fun JsonObject.propBool(field: KCallable<Boolean?>) {
    this { prop(field) { it.json() } }
}

fun JsonObject.propArray(field: KCallable<List<String>?>) {
    this {
        prop(field) {
            jsonArray(it.map { str -> str.json() })
        }
    }
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
    propWithAlt(field, altField.esNameFormat(), f)
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
