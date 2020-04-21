import com.melvic.archia.ast.compound.BoostMode
import com.melvic.archia.ast.compound.Modifier
import com.melvic.archia.ast.compound.ScoreMode
import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class FunctionScoreQueryTests : BehaviorSpec({
    given("function score query") {
        `when`("single function is provided") {
            then("it should not include a 'functions' field") {
                val output = evalQuery {
                    functionScore {
                        query { matchAll {} }
                        boost = "5"
                        randomScore {}
                        boostMode = BoostMode.MULTIPLY
                    }
                }.output()

                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "query": { "match_all": {} },
                                "boost": "5",
                                "boost_mode":"multiply",
                                "random_score": {}
                            }
                        }
                    }
                """.strip()
            }
        }
        `when`("several functions are combined") {
            then("the score functions should be specified in the functions field") {
                val output = evalQuery {
                    functionScore {
                        query { matchAll {} }
                        boost = "5"
                        function {
                            filter { term { "test" to "bar" } }
                            randomScore {  }
                            weight = 23
                        }
                        function {
                            filter { term { "test" to "cat" } }
                            weight = 42
                        }
                        maxBoost = 42
                        scoreMode = ScoreMode.MAX
                        boostMode = BoostMode.MULTIPLY
                        minScore = 42
                    }
                }.output()

                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "function_score": {
                              "query": { "match_all": {} },
                              "boost": "5", 
                              "functions": [
                                  {
                                      "filter": { "term": { "test": "bar" } },
                                      "weight": 23,
                                      "random_score": {} 
                                  },
                                  {
                                      "filter": { "term": { "test": "cat" } },
                                      "weight": 42
                                  }
                              ],
                              "max_boost": 42,
                              "score_mode": "max",
                              "boost_mode": "multiply",
                              "min_score" : 42
                            }
                        }
                    }
                """.strip()
            }
        }
        `when`("it contains a script score") {
            then("it should contain a script score field in the JSON output") {
                val output = evalQuery {
                    functionScore {
                        query {
                            term { "message" to "elasticsearch" }
                        }
                        scriptScore {
                            script {
                                source = "Math.log(2 + doc['likes'].value)"
                            }
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "query": {
                                    "term": { "message": "elasticsearch" }
                                },
                                "script_score" : {
                                    "script" : {
                                      "source": "Math.log(2 + doc['likes'].value)"
                                    }
                                }
                            }
                        }
                    }
                """.strip()
            }
        }
        `when`("it contains a script score with params") {
            then("it should render the script with the specified parameters") {
                val output = evalQuery {
                    functionScore {
                        query {
                            term { "message" to "elasticsearch" }
                        }
                        scriptScore {
                            script {
                                params {
                                    "a" to 5
                                    "b" to 1.2
                                }
                                source = "params.a / Math.pow(params.b, doc['likes'].value)"
                            }
                        }
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "query": {
                                    "term": { "message": "elasticsearch" }
                                },
                                "script_score" : {
                                    "script" : {
                                        "params": {
                                            "a": 5,
                                            "b": 1.2
                                        },
                                        "source": "params.a / Math.pow(params.b, doc['likes'].value)"
                                    }
                                }
                            }
                        }
                    }
                """.strip()
            }
        }
        `when`("the score function is random score") {
            then("it should (optionally) contain seed and field") {
                val output = evalQuery {
                    functionScore {
                        randomScore {
                            seed = 10
                            field = "_seq_no"
                        }
                    }
                }.output()
                output.mapTo(JsonStringOutput).strip() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "random_score": {
                                    "seed": 10,
                                    "field": "_seq_no"
                                }
                            }
                        }
                    }
                """.strip()
            }
        }
        `when`("the score function is field value factor") {
            then("it should contain field value factor fields") {
                val output = evalQuery {
                    functionScore {
                        fieldValueFactor {
                            field = "likes"
                            factor = 1.2f
                            modifier = Modifier.SQRT
                            missing = 1
                        }
                    }
                }
            }
        }
    }
})