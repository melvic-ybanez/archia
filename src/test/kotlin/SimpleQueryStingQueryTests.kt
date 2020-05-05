import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.leaf.fulltext.Operator
import com.melvic.archia.interpreter.missingField
import com.melvic.archia.interpreter.output
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SimpleQueryStingQueryTests : BehaviorSpec({
    given("simple query string query") {
        `when`("query parameter is not provided") {
            then("it should report a missing field error") {
                val result = evalQuery {
                    simpleQueryString {
                        fields = listOf("title", "body")
                        defaultOperator = Operator.AND
                    }
                }
                result shouldBe missingField("query")
            }
        }
        `when`("all required parameters are provided") {
            then("it should return the correct JSON structure") {
                val output = evalQuery {
                    simpleQueryString {
                        query = """\"fried eggs\" +(eggplant | potato) -frittata"""
                        fields = listOf("title^5", "body")
                        defaultOperator = Operator.AND
                    }
                }.output()

                assert(output, """
                    {
                      "query": {
                        "simple_query_string" : {
                            "query": "\"fried eggs\" +(eggplant | potato) -frittata",
                            "fields": ["title^5", "body"],
                            "default_operator": "and"
                        }
                      }
                    }
                """.trimIndent())
            }
        }
    }
})