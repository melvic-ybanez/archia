package com.melvic.archia.interpreter

import com.melvic.archia.Clause
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
class MissingField(val fieldName: String) : ErrorCode()
class UnknownQuery(val query: Clause) : ErrorCode()

fun errorMessage(code: ErrorCode) = when (code) {
    is MissingField -> "Missing field: ${code.fieldName}"
    is UnknownQuery -> "Unknown query: ${code.query}"
}

fun <R> missingField(callable: KCallable<R>) = MissingField(nameOf(callable))

fun <R> nameOf(callable: KCallable<R>): String = callable.name