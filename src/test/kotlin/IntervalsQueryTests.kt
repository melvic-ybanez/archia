import com.melvic.archia.ast.evalQuery
import io.kotest.core.spec.style.BehaviorSpec

class IntervalsQueryTests : BehaviorSpec({
    given("internal query") {
        `when`("the all_of rule is specified") {
            then("it should contain the properties of all_of rule") {
                val output = evalQuery {
                    intervals {
                        "my_text" {

                        }
                    }
                }
            }
        }
    }
})