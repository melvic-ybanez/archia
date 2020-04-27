import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BoolQueryTests : BehaviorSpec({
    given("bool query") {
        `when`("input is valid") {
            then("it should return the correct JSON string") {
                val output = evalQuery {
                    bool {
                        must {
                            term { "user" to "kimchy" }
                        }
                        filter {
                            term { "tag" to "tech" }
                        }
                        mustNot {
                            range {
                                "age" { gte = 10.es(); lte = 20.es() }
                            }
                        }
                        should {
                            term { "tag" to "wow"}
                            term { "tag" to "elasticsearch" }
                        }
                        minimumShouldMatch = 1.es()
                        boost = 1.0
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                      "query": {
                        "bool" : {
                          "must" : {
                            "term" : { "user" : "kimchy" }
                          },
                          "filter": {
                            "term" : { "tag" : "tech" }
                          },
                          "must_not" : {
                            "range" : {
                              "age" : { "gte" : 10, "lte" : 20 }
                            }
                          },
                          "should" : [
                            { "term" : { "tag" : "wow" } },
                            { "term" : { "tag" : "elasticsearch" } }
                          ],
                          "minimum_should_match" : 1,
                          "boost" : 1.0
                        }
                      }
                    }
                """.strip()
            }
        }
    }
})