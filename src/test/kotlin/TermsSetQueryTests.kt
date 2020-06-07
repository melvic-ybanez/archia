import io.kotest.core.spec.style.FeatureSpec

class TermsSetQueryTests : FeatureSpec({
    feature("terms set query") {
        scenario("should support querying array of terms") {
            assert {
                query {
                    termsSet {
                        "programming_languages" {
                            terms = listOf("c++", "java", "php")
                            minimumShouldMatchField = "required_matches"
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "terms_set": {
                                    "programming_languages": {
                                        "terms": ["c++", "java", "php"],
                                        "minimum_should_match_field": "required_matches"
                                    }
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }
        scenario("should support minimum-should-match-script parameter") {
            assert {
                query {
                    termsSet {
                        "programming_languages" {
                            terms = listOf("c++", "java", "php")
                            minimumShouldMatchScript {
                                source = "Math.min(params.num_terms, doc['required_matches'].value)"
                            }
                            boost = 1.0
                        }
                    }
                }
                expected = """
                    {
                        "query": {
                            "terms_set": {
                                "programming_languages": {
                                    "terms": ["c++", "java", "php"],
                                    "minimum_should_match_script": {
                                       "source": "Math.min(params.num_terms, doc['required_matches'].value)"
                                    },
                                    "boost": 1.0
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})