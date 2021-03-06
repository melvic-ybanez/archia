import com.melvic.archia.ast.compound.BoostMode
import com.melvic.archia.ast.compound.Modifier
import com.melvic.archia.ast.compound.MultiValueMode
import com.melvic.archia.ast.compound.ScoreMode
import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.leaf.geo.GeoString
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

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "query": { "match_all": {} },
                                "boost": "5",
                                "random_score": {},
                                "boost_mode":"multiply"
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
        `when`("several functions are combined") {
            then("the score functions should be specified in the functions field") {
                val output = evalQuery {
                    functionScore {
                        query { matchAll {} }
                        boost = "5"
                        functions(
                            {
                                filter { term { "test" to "bar" } }
                                randomScore {  }
                                weight = 23
                            },
                            {
                                filter { term { "test" to "cat" } }
                                weight = 42
                            }
                        )
                        maxBoost = 42
                        scoreMode = ScoreMode.MAX
                        boostMode = BoostMode.MULTIPLY
                        minScore = 42
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "function_score": {
                              "query": { "match_all": {} },
                              "boost": "5", 
                              "functions": [
                                  {
                                      "filter": { "term": { "test": "bar" } },
                                      "random_score": {},
                                      "weight": 23
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
                """.trimWhitespace()
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

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
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
                """.trimWhitespace()
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
                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
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
                """.trimWhitespace()
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
                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
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
                """.trimWhitespace()
            }
        }
        `when`("the score function is field value factor") {
            then("it should contain field value factor fields") {
                val output = evalQuery {
                    functionScore {
                        fieldValueFactor {
                            field = "likes"
                            factor = 1.2
                            modifier = Modifier.SQRT
                            missing = 1
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "field_value_factor": {
                                    "field": "likes",
                                    "factor": 1.2,
                                    "modifier": "sqrt",
                                    "missing": 1
                                }
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
        `when`("it has a decay function of gauss") {
            then("it should contain the gauss fields") {
                val output = evalQuery {
                    functionScore {
                        gauss {
                            "date" {
                                origin = date(2013, 9, 17)
                                scale = "10d"
                                offset = "5d"
                                decay = 0.5
                            }
                            multiValueMode = MultiValueMode.AVG
                        }
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                {
                    "query": {
                        "function_score": {
                            "gauss": {
                                "date": {
                                      "origin": "2013-09-17", 
                                      "scale": "10d",
                                      "offset": "5d", 
                                      "decay" : 0.5 
                                },
                                "multi_value_mode": "avg"
                            }
                        }
                    }
                }
            """.trimWhitespace()
            }
        }
        `when`("it has multiple decay functions") {
            then("it should contain all the decay functions in the `functions` field") {
                val output = evalQuery {
                    functionScore {
                        functions(
                            {
                                gauss {
                                    "price" {
                                        origin = "0".es()
                                        scale = "20"
                                    }
                                }
                            },
                            {
                                gauss {
                                    "location" {
                                        origin = GeoString(11, 12)
                                        scale = "2km"
                                    }
                                }
                            }
                        )
                        query {
                            term {
                                "properties" to "balcony"
                            }
                        }
                        scoreMode = ScoreMode.MULTIPLY
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "function_score": {
                                "functions": [
                                    {
                                      "gauss": {
                                        "price": {
                                          "origin": "0",
                                          "scale": "20"
                                        }
                                      }
                                    },
                                    {
                                      "gauss": {
                                        "location": {
                                          "origin": "11, 12",
                                          "scale": "2km"
                                        }
                                      }
                                    }
                                ],
                                "query": {
                                    "term": {
                                      "properties": "balcony"
                                    }
                                },
                                "score_mode": "multiply"
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})