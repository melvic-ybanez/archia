import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MatchPhraseQueryTests : BehaviorSpec({
    given("match phrase query") {
        `when`("query field is not provided") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    matchPhrase {
                        "message" {
                            analyzer = "my_analyzer"
                        }
                    }
                }
                result shouldBe missingField("query")
            }
        }
        `when`("all required fields are provided") {
            then("it should result to the correct JSON structure") {
                val output = evalQuery {
                    matchPhrase {
                        "message" {
                            query = "this is a test"
                            analyzer = "my_analyzer"
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "match_phrase" : {
                                "message" : {
                                    "query" : "this is a test",
                                    "analyzer" : "my_analyzer"
                                }
                            }
                        }
                    }
                """.strip()
            }
        }
    }
})