package com.melvic.archia.interpreter

import com.melvic.archia.output.JsonObject
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.json
import com.melvic.archia.script.Script

fun Script.interpret(): Evaluation {
    return json {
        propEnum(::lang)
        prop(::source) { it.json() }
        propWithAlt(::_params, ::params) {
            json {
                for ((key, value) in it) {
                    key to value.json()
                }
            }.success()
        }
    }.success()
}