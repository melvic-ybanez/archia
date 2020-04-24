import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class IntervalsQueryTests : BehaviorSpec({
    given("internal query") {
        `when`("the all_of rule is specified") {
            then("it should contain the properties of all_of rule") {
                val output = evalQuery {
                    intervals {
                        "my_text" {
                            allOf {
                                ordered = true
                                intervals {
                                    match {
                                        query = "my favorite food"
                                        maxGaps = 0
                                        ordered = true
                                    }
                                    anyOf {
                                        intervals {
                                            match { query = "hot water" }
                                            match { query = "cold porridge" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                      "query": {
                        "intervals" : {
                          "my_text" : {
                            "all_of" : {
                              "ordered" : true,
                              "intervals" : [
                                {
                                  "match" : {
                                    "query" : "my favorite food",
                                    "max_gaps" : 0,
                                    "ordered" : true
                                  }
                                },
                                {
                                  "any_of" : {
                                    "intervals" : [
                                      { "match" : { "query" : "hot water" } },
                                      { "match" : { "query" : "cold porridge" } }
                                    ]
                                  }
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                """.strip()
            }
        }
    }
})