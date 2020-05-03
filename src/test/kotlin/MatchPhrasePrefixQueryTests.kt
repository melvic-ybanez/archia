import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MatchPhrasePrefixQueryTests : BehaviorSpec({
    given("match phrase prefix query") {
        `when`("query field is not provided") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    matchPhrasePrefix {
                        "message" {
                            analyzer = "my_analyzer"
                        }
                    }
                }
                result shouldBe missingField("query")
            }
        }
        `when`("all required fields are provided") {
            then("it should return the correct JSON structure") {
                val output = evalQuery {
                    matchPhrasePrefix {
                        "message" {
                            query = "quick brown f"
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "match_phrase_prefix" : {
                                "message" : {
                                    "query" : "quick brown f"
                                }
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})