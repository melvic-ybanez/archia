import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.to
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class TermQueryTests : BehaviorSpec({
    given("term query") {
        `when`("field parameter is not supplied") {
            then("it should report missing field error") {
                val result = evalQuery { term {  } }
                result shouldBe missingField("field")
            }
        }
        `when`("value paramater of field is not supplied") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    term { "user" { boost = 1.2f }}
                }
                result shouldBe missingField("value")
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
                val output = result.output().to(JsonStringOutput)
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
                val output = result.output().to(JsonStringOutput)
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