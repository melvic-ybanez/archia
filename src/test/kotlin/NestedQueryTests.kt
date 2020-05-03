import com.melvic.archia.ast.joining.ScoreMode
import com.melvic.archia.interpreter.MissingField
import com.melvic.archia.interpreter.missingFieldCode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FeatureSpec

class NestedQueryTests : FeatureSpec({
    feature("nested query") {
        scenario("should require path and query fields") {
            assert {
                query {
                    nested {
                        scoreMode = ScoreMode.AVG
                    }
                }
                errors = listOf(MissingField("path"), MissingField("query"))
            }
        }
    }
})