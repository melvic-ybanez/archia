import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.Failed
import com.melvic.archia.interpreter.MissingField
import com.melvic.archia.interpreter.missingFieldCode
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BoostingQueryTests : BehaviorSpec({
    given("boosting query") {
        `when`("there are missing required fields") {
            then("report missing field errors") {
                val result = evalQuery {
                    boosting { negativeBoost = 0.5 }
                }
                result shouldBe Failed(mutableListOf(
                    MissingField("positive"),
                    MissingField("negative")
                ))
            }
        }
        `when`("all required fields are supplied") {
            then("it should return the correct JSON structure") {
                val output = evalQuery {
                    boosting {
                        positive {
                            term { "text" to "apple" }
                        }
                        negative {
                            term { "text" to "pie tart fruit crumble tree" }
                        }
                        negativeBoost = 0.5
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "boosting" : {
                                "positive" : {
                                    "term" : {
                                        "text" : "apple"
                                    }
                                },
                                "negative" : {
                                     "term" : {
                                         "text" : "pie tart fruit crumble tree"
                                    }
                                },
                                "negative_boost" : 0.5
                            }
                        }
                    }
                """.strip()
            }
        }
    }
})