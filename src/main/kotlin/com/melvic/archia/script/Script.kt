package com.melvic.archia.script

import com.melvic.archia.ast.Clause
import com.melvic.archia.ast.Init

data class Script(
    var lang: ScriptLang? = null,
    var source: String? = null,
    var _params: MutableMap<String, Number>? = null
) : Clause {
    fun params(init: Init<Params>) {
        _params = Params().apply(init).params
    }
}

enum class ScriptLang {
    PAINLESS, EXPRESSION, MUSTACHE, JAVA
}

data class Params(val params: MutableMap<String, Number> = mutableMapOf()) {
    infix fun String.to(num: Number) {
        params[this] = num
    }
}