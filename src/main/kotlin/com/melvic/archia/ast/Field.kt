package com.melvic.archia.ast

import kotlin.reflect.KProperty

open class Field(var name: String) : TreeNode()

abstract class WithField<F : Field> : Clause() {
    var field: F by parameters

    /**
     * Constructs an elasticsearch field from a string
     */
    operator fun String.invoke(init: Init<F>) {
        field = initField(this).apply(init)
    }

    abstract fun initField(name: String): F

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::field)
}

abstract class WithShortForm<F : Field, V> : WithField<F>() {
    var customProp: Param<V>? = null

    infix fun String.to(_value: V) {
        customProp = Pair(this, _value)
        this { this.updateValue(_value) }
    }

    abstract fun F.updateValue(value: V)
}