import com.melvic.archia.ast.evalQuery
import com.melvic.archia.interpreter.Failed
import com.melvic.archia.interpreter.MissingField
import com.melvic.archia.interpreter.missingFieldCode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BoostingQueryTests : BehaviorSpec({
    given("boosting query") {
        `when`("there are missing required fields") {
            then("report missing field errors") {
                val result = evalQuery {
                    boosting { negativeBoost = 0.5f }
                }
                result shouldBe Failed(mutableListOf(
                    MissingField("positive"),
                    MissingField("negative")
                ))
            }
        }
    }
})