import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ConstantScoreTests : BehaviorSpec({
    given("constant score query") {
        `when`("the field parameter is not supplied") {
            then("it should report missing field error") {
                val result = evalQuery {
                    constantScore { boost = 1.2 }
                }
                result shouldBe missingField("filter")
            }
        }
        `when`("all required parameters are supplied") {
            then("it should return the correct JSON structure") {
                val output = evalQuery {
                    constantScore {
                        filter {
                            term { "user" to "kimchy" }
                        }
                        boost = 1.2
                    }
                }.output()
                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "constant_score" : {
                                "filter" : {
                                    "term" : { "user" : "kimchy"}
                                },
                                "boost" : 1.2
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})