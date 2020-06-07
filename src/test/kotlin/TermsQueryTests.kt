import io.kotest.core.spec.style.FeatureSpec

class TermsQueryTests : FeatureSpec({
    feature("terms query") {
        scenario("should query multiple terms for the field") {
            assert {
                query {
                    terms {
                        "user" to listOf("kimchy", "elasticsearch")
                        boost = 1.0
                    }
                }
                expected = """
                    {
                        "query" : {
                            "terms" : {
                                "user" : ["kimchy", "elasticsearch"],
                                "boost" : 1.0
                            }
                        }
                    }
                """.trimIndent()
            }
        }
        scenario("should support term lookup") {
            assert {
                query {
                    terms {
                        "color" {
                            index = "my_index"
                            id = "2"
                            path = "color"
                        }
                    }
                }
                expected = """
                    {
                      "query": {
                        "terms": {
                            "color" : {
                                "index" : "my_index",
                                "id" : "2",
                                "path" : "color"
                            }
                        }
                      }
                    }
                """.trimIndent()
            }
        }
    }
})