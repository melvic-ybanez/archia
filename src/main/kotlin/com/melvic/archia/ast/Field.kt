package com.melvic.archia.ast

open class Field(var name: String)

open class WithField<F : Field> : Clause {
    var field: F? = null
}