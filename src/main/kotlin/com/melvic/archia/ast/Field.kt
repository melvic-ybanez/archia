package com.melvic.archia.ast

open class Field(var name: String)

abstract class WithField<F : Field> : Clause {
    var field: F? = null

    /**
     * Constructs an elasticsearch match field from a string
     */
    operator fun String.invoke(init: Init<F>) {
        field = getField(this).apply(init)
    }

    abstract fun getField(name: String): F
}