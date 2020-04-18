package com.melvic.archia.interpreter

import com.melvic.archia.ast.Clause
import kotlin.reflect.KCallable

sealed class Result<out A> {
    abstract fun value(): A
}

class Failed(val code: ErrorCode) : Result<Nothing>() {
    override fun value(): Nothing = throw Exception("Not allowed")
}

class Success<A>(private val init: A) : Result<A>() {
    override fun value(): A = init
}

sealed class ErrorCode
data class MissingField(val fieldName: String) : ErrorCode()
data class UnknownQuery(val query: Clause) : ErrorCode()
data class InvalidValue<A>(val fieldName: String, val value: A) : ErrorCode()

fun errorMessage(code: ErrorCode) = when (code) {
    is MissingField -> "Missing field: ${code.fieldName}"
    is UnknownQuery -> "Unknown query: ${code.query}"
    is InvalidValue<*> -> "Invalid value. Field: ${code.fieldName}. Value: ${code.value}"
}

fun <R> missingField(callable: KCallable<R>) = MissingField(nameOf(callable)).fail()

fun <R, A> invalidValue(callable: KCallable<R>, value: A) = InvalidValue(nameOf(callable), value).fail()

fun ErrorCode.fail() = Failed(this)

fun <A> A.success() = Success(this)