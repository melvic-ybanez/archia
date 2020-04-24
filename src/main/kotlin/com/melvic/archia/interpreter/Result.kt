package com.melvic.archia.interpreter

import com.melvic.archia.ast.Clause
import com.melvic.archia.output.JsonArray
import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import kotlin.reflect.KCallable

sealed class Result<out A> {
    abstract fun value(): A
}

data class Failed(val errors: List<ErrorCode>) : Result<Nothing>() {
    override fun value(): Nothing = throw Exception("Not allowed")

    fun show(): JsonArray {
        return JsonArray(errors.map {
            json {
                "code" to it.show().json()
                "message" to errorMessageOf(it).json()
            }
        }.toMutableList())
    }
}

data class Success<A>(private val init: A) : Result<A>() {
    override fun value(): A = init
}

sealed class ErrorCode
data class MissingField(val fieldName: String) : ErrorCode()
data class UnknownQuery(val query: Clause) : ErrorCode()
data class InvalidValue<A>(val fieldName: String, val value: A) : ErrorCode()

fun ErrorCode.show(): String {
    return when (this) {
        is MissingField -> MissingField::class.esNameFormat()
        is UnknownQuery -> UnknownQuery::class.esNameFormat()
        is InvalidValue<*> -> InvalidValue::class.esNameFormat()
    }
}

fun errorMessageOf(code: ErrorCode) = when (code) {
    is MissingField -> "Missing field: ${code.fieldName}"
    is UnknownQuery -> "Unknown query: ${code.query}"
    is InvalidValue<*> -> "Invalid value. Field: ${code.fieldName}. Value: ${code.value}"
}

fun <R> missingField(callable: KCallable<R>) = missingFieldCode(callable).fail()

fun missingField(fieldName: String) = MissingField(fieldName).fail()

fun <R> missingFieldCode(callable: KCallable<R>) = MissingField(callable.esNameFormat())

fun ErrorCode.fail() = Failed(listOf(this))

fun <A> A.success() = Success(this)

fun Evaluation.output(): JsonValue = when (this) {
    is Failed -> show()
    is Success<*> -> value()
}