import com.melvic.archia.ast.ClauseBuilder
import com.melvic.archia.ast.Init
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.transform
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class TermQueryTests : BehaviorSpec({
    given("term query") {
        fun evalQuery(init: Init<ClauseBuilder>) = interpret {
            query(init)
        }

        `when`("field parameter is not supplied") {
            then("it should return missing field error") {
                val result = evalQuery { term {  } }
                result shouldBe missingField("field")
            }
        }
        `when`("all required parameters are supplied") {
            then("it should produce the correct json string") {
                val result = evalQuery {
                    term {
                        "user" {
                            value = "Kimchy"
                            boost = 1.0f
                        }
                    }
                }
                val output = result.output().transform(JsonStringOutput)
                output.strip() shouldBe """
                    {
                        "query": {
                            "term": {
                                "user": {
                                    "value": "Kimchy",
                                    "boost": 1.0
                                }
                            }
                        }
                    }
                """.trimIndent().strip()
            }
        }
        `when`("in simplified form") {
            then("it should not display the field param") {
                val result = evalQuery {
                    term { "user" to "melvic" }
                }
                val output = result.output().transform(JsonStringOutput)
                output.strip() shouldBe """
                    {
                        "query": {
                            "term": { "user": "melvic" }
                        }
                    }
                """.trimIndent().strip()
            }
        }
    }
})