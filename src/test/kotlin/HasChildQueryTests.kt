import com.melvic.archia.ast.leaf.joining.ScoreMode
import com.melvic.archia.interpreter.MissingField
import io.kotest.core.spec.style.FeatureSpec

class HasChildQueryTests : FeatureSpec({
    feature("has-child query") {
        scenario("should require type and query fields") {
            assertFail {
                query {
                    hasChild {
                        maxChildren = 10
                        minChildren = 2
                        scoreMode = ScoreMode.MIN
                    }
                }
                errors = listOf(MissingField("type"), MissingField("query"))
            }
        }
    }
})