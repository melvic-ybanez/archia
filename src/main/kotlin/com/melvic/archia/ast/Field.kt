package com.melvic.archia.ast

open class Field(var name: String) : TreeNode()

abstract class WithField<F : Field> : Clause() {
    var field: F? = null

    /**
     * Constructs an elasticsearch match field from a string
     */
    operator fun String.invoke(init: Init<F>) {
        field = getField(this).apply(init)
    }

    abstract fun getField(name: String): F
}

abstract class WithShortForm<F : Field, V> : WithField<F>() {
    var namedProp: Pair<String, V>? = null

    infix fun String.to(_value: V) {
        namedProp = Pair(this, _value)
        this { this.updateValue(_value) }
    }

    abstract fun F.updateValue(value: V)
}