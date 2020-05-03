import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CommonTermsQueryTests : BehaviorSpec({
    given("common terms query") {
        `when`("a regular minimum-should-match is provided") {
            then("it should not contain low and high frequencies") {
                val output = evalQuery {
                    common {
                        "body" {
                            query = "nelly the elephant as a cartoon"
                            cutoffFrequency = 0.001
                            minimumShouldMatch = 2.es()
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "common": {
                                "body": {
                                    "query": "nelly the elephant as a cartoon",
                                    "cutoff_frequency": 0.001,
                                    "minimum_should_match": 2
                                }
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
        `when`("the minimun-should-match contains low and high frequency") {
            then("it should be provided through method invocation") {
                val output = evalQuery {
                    common {
                        "body" {
                            query = "nelly the elephant not as a cartoon"
                            cutoffFrequency = 0.001
                            minimumShouldMatch {
                                lowFreq = 2.es()
                                highFreq = 3.es()
                            }
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "common": {
                                "body": {
                                    "query": "nelly the elephant not as a cartoon",
                                    "cutoff_frequency": 0.001,
                                    "minimum_should_match": {
                                        "low_freq" : 2,
                                        "high_freq" : 3
                                    }
                                }
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})