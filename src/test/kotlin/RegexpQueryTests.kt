import com.melvic.archia.ast.All
import com.melvic.archia.ast.Rewrite
import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class RegexpQueryTests : FeatureSpec({
    feature("regex query") {
        scenario("should require value field") {
            assertFail {
                query {
                    regexp {
                        "user" {
                            flags = All
                            maxDeterminedStates = 1000
                            rewrite = Rewrite.CONSTANT_SCORE
                        }
                    }
                }
                errors = listOf(MissingField("value"))
            }
        }
    }
})