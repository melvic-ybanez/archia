import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.fulltext.Operator
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
                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
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
                """.trimWhitespace()
            }
        }
        `when`("short form is provided") {
            then("it should not require the query param of the field") {
                val output = evalQuery {
                    match {
                        "message" to "this is a test".es()
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "match" : {
                                "message" : "this is a test"
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})