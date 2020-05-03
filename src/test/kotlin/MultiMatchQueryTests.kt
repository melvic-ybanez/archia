import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.fulltext.MultiMatchType
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MultiMatchQueryTests : BehaviorSpec({
    given("multi match query") {
        `when`("it is in simple form") {
            then("do not include the extra parameters in the result") {
                val output = evalQuery {
                    multiMatch {
                        query = "Will Smith"
                        fields = listOf("title", "*_name")
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                      "query": {
                        "multi_match" : {
                          "query":    "Will Smith",
                          "fields": [ "title", "*_name" ] 
                        }
                      }
                    }
                """.trimWhitespace()
            }
        }
        `when`("a type is provided") {
            then("it contain the type and other related params in the result") {
                val output = evalQuery {
                    multiMatch {
                        query = "brown fox"
                        type = MultiMatchType.BEST_FIELDS
                        fields = listOf("subject", "message")
                        tieBreaker = 0.3
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                      "query": {
                        "multi_match" : {
                          "query":      "brown fox",
                          "type":       "best_fields",
                          "fields":     [ "subject", "message" ],
                          "tie_breaker": 0.3
                        }
                      }
                    }
                """.trimWhitespace()
            }
        }
    }
})