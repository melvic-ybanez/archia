import com.melvic.archia.ast.Fuzziness
import com.melvic.archia.ast.Fuzziness.Auto
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class FuzzyQueryTests : FeatureSpec({
    feature("fuzzy query") {
        scenario("should require the value field") {
            assertFail {
                query {
                    fuzzy {
                        "user" {
                            fuzziness = Auto()
                        }
                    }
                }
                errors = listOf(MissingField("value"))
            }
        }
        scenario("should be valid if all required fields are provided") {
            assert {
                query {
                    fuzzy {
                        "user" {
                            value = "ki"
                            fuzziness = Auto()
                            maxExpansions = 50
                            prefixLength = 0
                            transpositions = true
                            rewrite = Rewrite.CONSTANT_SCORE
                        }
                    }
                }
                expected = """
                    {
                        "query": {
                            "fuzzy": {
                                "user": {
                                    "value": "ki",
                                    "fuzziness": "AUTO",
                                    "max_expansions": 50,
                                    "prefix_length": 0,
                                    "transpositions": true,
                                    "rewrite": "constant_score"
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})