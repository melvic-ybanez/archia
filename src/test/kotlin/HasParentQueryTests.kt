import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class HasParentQueryTests : FeatureSpec({
    feature("has-parent query") {
        scenario("should require parent type and query fields") {
            assertFail {
                query {
                    hasParent {}
                }
                errors = listOf(MissingField("parent_type"), MissingField("query"))
            }
        }
        scenario("should return correct JSON if all required fields are provided") {
            assert {
                query {
                    hasParent {
                        parentType = "parent"
                        query {
                            term {
                                "tag" {
                                    value = "Elasticsearch"
                                }
                            }
                        }
                    }
                }
                expected = """
                    {
                        "query": {
                            "has_parent" : {
                                "parent_type" : "parent",
                                "query" : {
                                    "term" : {
                                        "tag" : {
                                            "value" : "Elasticsearch"
                                        }
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
        }
    }
})