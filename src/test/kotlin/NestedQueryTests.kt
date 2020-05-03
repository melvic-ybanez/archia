import com.melvic.archia.ast.joining.ScoreMode
import com.melvic.archia.interpreter.MissingField
import com.melvic.archia.interpreter.missingFieldCode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FeatureSpec

class NestedQueryTests : FeatureSpec({
    feature("nested query") {
        scenario("should require path and query fields") {
            assert {
                query {
                    nested {
                        scoreMode = ScoreMode.AVG
                    }
                }
                errors = listOf(MissingField("path"), MissingField("query"))
            }
        }
        scenario("should return correct JSON if all required fields are provided") {
            assert {
                query {
                    nested {
                        path = "obj1"
                        query {
                            bool {
                                must {
                                    match { "obj1.name" to "blue".es() }
                                    range { "obj1.count" { gt = 5.es() } }
                                }
                            }
                        }
                        scoreMode = ScoreMode.AVG
                    }
                }
                expected = """
                    {
                        "query":  {
                            "nested" : {
                                "path" : "obj1",
                                "query" : {
                                    "bool" : {
                                        "must" : [
                                        { "match" : {"obj1.name" : "blue"} },
                                        { "range" : {"obj1.count" : {"gt" : 5}} }
                                        ]
                                    }
                                },
                                "score_mode" : "avg"
                            }
                        }
                    }
                """.trimIndent()
            }
        }
        scenario("should support multi-level nesting") {
            assert {
                query {
                    nested {
                        path = "driver"
                        query {
                            nested {
                                path = "driver.vehicle"
                                query {
                                    bool {
                                        must {
                                            match { "driver.vehicle.make" to "Powell Motors".es() }
                                            match { "driver.vehicle.model" to "Canyonero".es() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                expected = """
                    {
                        "query" : {
                            "nested" : {
                                "path" : "driver",
                                "query" : {
                                    "nested" : {
                                        "path" :  "driver.vehicle",
                                        "query" :  {
                                            "bool" : {
                                                "must" : [
                                                    { "match" : { "driver.vehicle.make" : "Powell Motors" } },
                                                    { "match" : { "driver.vehicle.model" : "Canyonero" } }
                                                ]
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})