import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.leaf.Operator
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MatchQueryTests : BehaviorSpec({
    given("match query") {
        `when`("field parameter is not supplied") {
            then("it should return missing field error") {
                val result = evalQuery { match {} }
                result shouldBe missingField("field")
            }
        }
        `when`("query parameter of field is not supplied") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    match { "message" { analyzer = "" } }
                }
                result shouldBe missingField("query")
            }
        }
        `when`("all required fields are supplied") {
            then("it should return the correct JSON string") {
                val output = evalQuery {
                    match {
                        "message" {
                            query = "this is a test".es()
                            operator = Operator.AND
                        }
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "match" : {
                                "message" : {
                                    "query" : "this is a test",
                                    "operator" : "and"
                                }
                            }
                        }
                    }
                """.trimIndent().strip()
            }
        }
    }
})