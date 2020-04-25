package com.melvic.archia.interpreter

import com.melvic.archia.output.json
import com.melvic.archia.script.Script

fun Script.interpret(): Evaluation {
    return json {
        propEnum(::lang)
        propWithAlt(::_params, ::params) {
            json {
                for ((key, value) in it) {
                    key to value.json()
                }
            }.success()
        }
        propStr(::source)
    }.success()
}