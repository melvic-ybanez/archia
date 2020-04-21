import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class DisMaxQueryTests : BehaviorSpec({
    given("disjunction max query") {
        `when`("the queries parameter is not supplied") {
            then("it should report missing field error") {
                val result = evalQuery {
                    disMax { tieBreaker = 0.7 }
                }
                result shouldBe missingField("queries")
            }
        }
        `when`("all the required parameters are supplied") {
            then("it should report the correct JSON structure") {
                val output = evalQuery {
                    disMax {
                        queries {
                            term { "title" to "Quick pets" }
                            term { "body" to "Quick pets" }
                        }
                        tieBreaker = 0.7
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "dis_max" : {
                                "queries" : [
                                    { "term" : { "title" : "Quick pets" }},
                                    { "term" : { "body" : "Quick pets" }}
                                ],
                                "tie_breaker" : 0.7
                            }
                        }
                    }
                """.strip()
            }
        }
    }
})