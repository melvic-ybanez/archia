import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MatchBoolPrefixQueryTests : BehaviorSpec({
    given("match bool prefix query") {
        `when`("query is not provided") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    matchBoolPrefix {
                        "message" { analyzer = "keyword" }
                    }
                }
                result shouldBe missingField("query")
            }
        }
        `when`("it has all the required fields") {
            then("it should produce JSON object correctly") {
                val output = evalQuery {
                    matchBoolPrefix {
                        "message" {
                            query = "quick brown f"
                            analyzer = "keyword"
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "match_bool_prefix" : {
                                "message": {
                                    "query": "quick brown f",
                                    "analyzer": "keyword"
                                }
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})