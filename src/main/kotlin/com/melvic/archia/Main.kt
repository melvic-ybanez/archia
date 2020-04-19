package main.kotlin.com.melvic.archia

import main.kotlin.com.melvic.archia.ast.buildQuery
import main.kotlin.com.melvic.archia.interpreter.Failed
import main.kotlin.com.melvic.archia.interpreter.Success
import main.kotlin.com.melvic.archia.interpreter.interpret
import main.kotlin.com.melvic.archia.output.JsonStringOutput
import main.kotlin.com.melvic.archia.output.transform

fun main() {
    val result = interpret {
        query {
            boosting {
                positive {
                    term {
                        "text" to "apple"
                    }
                }
                negative {
                    term {
                        "text" to "pie tart fruit crumble tree"
                    }
                }
                negativeBoost = 0.5f
            }
        }
    }
    val output = when (result) {
        is Failed -> result.show()
        is Success<*> -> result.value()
    }
    println(output.transform(JsonStringOutput))
}

fun boolQuery() = buildQuery {
    query {
        bool {
            must {
                term { "user" to "melvic" }
            }
            filter { term { "tag" to "tech" } }
            mustNot {
                range {
                    "age" { gte = 10.es(); lte = 20.es() }
                }
            }
            should {
                term { "message" to "go beyond plus ultra" }
                term {
                    "tag" {
                        value = "elasticsearch"
                        boost = 1.4f
                    }
                }
            }
            minimumShouldMatch = 1.es()
            boost = 1.0f
        }
    }
}