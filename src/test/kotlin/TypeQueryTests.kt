import io.kotest.core.spec.style.FeatureSpec

class TypeQueryTests : FeatureSpec({
    feature("type query") {
        scenario("should support value field") {
            assert {
                query {
                    type {
                        value = "_doc"
                    }
                }
                expected = """
                    {
                        "query": {
                            "type" : {
                                "value" : "_doc"
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})