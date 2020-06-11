import com.melvic.archia.ast.Rewrite
import io.kotest.core.spec.style.FeatureSpec

class WildcardQueryTests : FeatureSpec({
    feature("wildcard query") {
        scenario("should support value, boost, and rewrite parameters") {
            assert {
                query {
                    wildcard {
                        "user" {
                            value = "ki*y"
                            boost = 1.0
                            rewrite = Rewrite.CONSTANT_SCORE
                        }
                    }
                }
                expected = """
                    {
                        "query": {
                            "wildcard": {
                                "user": {
                                    "value": "ki*y",
                                    "boost": 1.0,
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