import com.melvic.archia.ast.All
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class RegexpQueryTests : FeatureSpec({
    feature("regex query") {
        scenario("should require value field") {
            assertFail {
                query {
                    regexp {
                        "user" {
                            flags = All
                            maxDeterminizedStates = 1000
                            rewrite = Rewrite.CONSTANT_SCORE
                        }
                    }
                }
                errors = listOf(MissingField("value"))
            }
        }
        scenario("should be valid if all required fields are provided") {
            assert {
                query {
                    regexp {
                        "user" {
                            value = "k.*y"
                            flags = All
                            maxDeterminizedStates = 10000
                            rewrite = Rewrite.CONSTANT_SCORE
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "regexp": {
                                    "user": {
                                        "value": "k.*y",
                                        "flags" : "ALL",
                                        "max_determinized_states": 10000,
                                        "rewrite": "constant_score"
                                    }
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }
    }
})