package com.melvic.archia.interpreter

import com.melvic.archia.ast.Clause
import com.melvic.archia.output.JsonArray
import com.melvic.archia.output.JsonObject
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
        is MissingField -> snakeCaseNameOf(MissingField::class)
        is UnknownQuery -> snakeCaseNameOf(UnknownQuery::class)
        is InvalidValue<*> -> snakeCaseNameOf(InvalidValue::class)
    }
}

fun errorMessageOf(code: ErrorCode) = when (code) {
    is MissingField -> "Missing field: ${code.fieldName}"
    is UnknownQuery -> "Unknown query: ${code.query}"
    is InvalidValue<*> -> "Invalid value. Field: ${code.fieldName}. Value: ${code.value}"
}

fun <R> missingField(callable: KCallable<R>) = missingFieldCode(callable).fail()

fun <R> missingFieldCode(callable: KCallable<R>) = MissingField(nameOf(callable))

fun ErrorCode.fail() = Failed(listOf(this))

fun <A> A.success() = Success(this)

fun JsonObject.validate(): Evaluation {
    return if (this.errors.isEmpty()) {
        this.success()
    } else Failed(this.errors)
}