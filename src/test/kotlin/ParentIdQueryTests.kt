import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class ParentIdQueryTests : FeatureSpec({
    feature("parent id query") {
        scenario("should require type and id fields") {
            assertFail {
                query {
                    parentId { ignoreUnmapped = false }
                }
                errors = listOf(MissingField("type"), MissingField("id"))
            }
        }
        scenario("should be valid if all required fields are provided") {
            assert {
                query {
                    parentId {
                        type = "my-child"
                        id = "1"
                    }
                }
                expected = """
                    {
                      "query": {
                          "parent_id": {
                              "type": "my-child",
                              "id": "1"
                          }
                      }
                    }
                """.trimIndent()
            }
        }
    }
})