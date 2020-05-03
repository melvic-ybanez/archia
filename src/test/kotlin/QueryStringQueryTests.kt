import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.mapTo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class QueryStringQueryTests : BehaviorSpec({
    given("query string") {
        `when`("all required fields are provided") {
            then("it should return the valid JSON structure") {
                val output = evalQuery {
                    queryString {
                        query = "(new york city) OR (big apple)"
                        defaultField = "content"
                    }
                }.output()

                output.mapTo(JsonStringOutput).trimWhitespace() shouldBe """
                    {
                        "query": {
                            "query_string" : {
                                "query" : "(new york city) OR (big apple)",
                                "default_field" : "content"
                            }
                        }
                    }
                """.trimWhitespace()
            }
        }
    }
})