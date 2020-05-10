import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class IdsQueryTests : FeatureSpec({
    feature("ids query") {
        scenario("should require values field") {
            assertFail {
                query {
                    ids {  }
                }
                errors = listOf(MissingField("values"))
            }
        }
        scenario("should be valid if all required params are provided") {
            assert {
                query {
                    ids {
                        values = listOf("1", "4", "100")
                    }
                }
                expected = """
                    {
                        "query": {
                            "ids" : {
                                "values" : ["1", "4", "100"]
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})