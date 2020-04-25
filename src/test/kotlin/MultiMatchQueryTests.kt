import com.melvic.archia.ast.evalQuery
import io.kotest.core.spec.style.BehaviorSpec

class MultiMatchQueryTests : BehaviorSpec({
    given("multi match query") {
        `when`("it is in simple form") {
            then("do not include the extra parameters in the result") {
                val output = evalQuery {
                    multiMatch {
                        query = "Will Smith"
                        fields = listOf("title", "*_name")
                    }
                }
            }
        }
    }
})