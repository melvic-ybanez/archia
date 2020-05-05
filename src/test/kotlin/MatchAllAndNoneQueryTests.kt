import io.kotest.core.spec.style.FeatureSpec

class MatchAllAndNoneQueryTests : FeatureSpec({
    feature("match all query") {
        scenario("should be valid if no params are given") {
            assert {
                query {
                    matchAll {  }
                }
                expected = """
                    {
                        "query": {
                            "match_all": {}
                        }
                    }
                """.trimIndent()
            }
        }
        scenario("should accept optional boost parameter") {
            assert {
                query {
                    matchAll { boost = 1.2 }
                }
                expected = """
                    {
                        "query": {
                            "match_all": { "boost" : 1.2 }
                        }
                    }
                """.trimIndent()
            }
        }
    }
    feature("match none query") {
        scenario("should be as simple as match all with no boosts") {
            assert {
                query {
                    matchNone {  }
                }
                expected = """
                    {
                        "query": {
                            "match_none": {}
                        }
                    }
                """.trimIndent()
            }
        }
    }
})