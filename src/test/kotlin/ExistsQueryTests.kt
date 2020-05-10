import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class ExistsQueryTests : FeatureSpec({
    feature("exists query") {
        scenario("should require the field param") {
            assertFail {
                query {
                    exists {  }
                }
                errors = listOf(MissingField("field"))
            }
        }
        scenario("should be valid if all required fields are provided") {
            assert {
                query {
                    exists {
                        field = "user"
                    }
                }
                expected = """
                    {
                        "query": {
                            "exists": {
                                "field": "user"
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})