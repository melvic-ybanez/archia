import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class RangeQueryTests : BehaviorSpec({
    given("range query") {
        `when`("field parameter is not supplied") {
            then("it should report a missing field error") {
                val result = evalQuery { range {} }
                result shouldBe missingField("field")
            }
        }
        `when`("all required parameters are supplied") {
            then("it should return the correct JSON string") {
                val output = evalQuery {
                    range {
                        "age" {
                            gte = 10.es()
                            lte = 20.es()
                            boost = 2.0
                        }
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "range" : {
                                "age" : {
                                    "gte" : 10,
                                    "lte" : 20,
                                    "boost" : 2.0
                                }
                            }
                        }
                    }
                """.strip()
            }
        }
    }
})