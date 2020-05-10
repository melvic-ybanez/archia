import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class PrefixQueryTests : FeatureSpec({
    feature("prefix query") {
        scenario("should require 'field' field") {
            assertFail {
                query {
                    prefix {}
                }
                errors = listOf(MissingField("field"))
            }
        }
        scenario("should require value field") {
            assertFail {
                query {
                    prefix {
                        "user" {}
                    }
                }
                errors = listOf(MissingField("value"))
            }
        }
        scenario("should be valid if all required fields are provided") {
            assert {
                query {
                    prefix {
                        "user" {
                            value = "ki"
                        }
                    }
                }
                expected = """
                    {
                        "query": {
                            "prefix": {
                                "user": {
                                    "value": "ki"
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
        }
        scenario("should support short form requests") {
            assert {
                query {
                    prefix {
                        "user" to "ki"
                    }
                }
                expected = """
                    {
                        "query": {
                            "prefix" : { "user" : "ki" }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})