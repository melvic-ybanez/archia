import com.melvic.archia.ast.evalQuery
import com.melvic.archia.ast.fulltext.Operator
import com.melvic.archia.interpreter.missingField
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
    }
})