package com.melvic.archia.ast

import com.melvic.archia.interpreter.interpret
import com.melvic.archia.interpreter.toSnakeCase
import kotlin.reflect.KProperty

typealias Init<A> = A.() -> Unit

data class Query(var clause: Clause? = null): Clause() {
    fun query(init: Init<ClauseBuilder>) {
        val builder = ClauseBuilder().apply(init)
        clause = builder.clause
    }
}

open class Clause : TreeNode() {
    /**
     * Returns the name of the clause in lowercase snake format,
     * removing the "Query" suffix, if one is found.
     */
    open fun esName(): String {
        val simpleName = this::class.java.simpleName
        val noPrefix = simpleName.substringBeforeLast("Query")
        return noPrefix.toSnakeCase()
    }

    open val topLevel: Boolean = true
}

open class TreeNode {
    val parameters: MutableMap<String, Any?> = mutableMapOf()
    open val requiredParams: List<KProperty<Any>> = listOf()
}

open class WithQuery : Clause(), BuilderHelper {
    var query: Clause by parameters

    fun query(init: Init<ClauseBuilder>) {
        setClause(init) { query = it }
    }

    override val requiredParams: List<KProperty<Any>>
        get() = extraRequiredParams + listOf(::query)

    open val extraRequiredParams: List<KProperty<Any>> = listOf()
}

typealias MultiClause = MutableList<Clause>

fun buildQuery(init: Init<Query>) = Query().apply(init)

fun evalQuery(init: Init<ClauseBuilder>) = interpret {
    query(init)
}