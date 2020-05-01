import com.melvic.archia.ast.*
import com.melvic.archia.interpreter.Evaluation
import com.melvic.archia.interpreter.interpret
import com.melvic.archia.interpreter.output
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.mapTo
import io.kotest.matchers.shouldBe

data class TestData(
    var query: Clause? = null,
    var expected: String? = null
) : BuilderHelper {
    fun query(init: Init<ClauseBuilder>) {
        setClause(init) { query = it }
    }
}

fun String.strip(): String {
    return trimIndent().replace(" ", "").replace("\n", "")
}

fun assert(result: JsonValue, expected: String) {
    result.mapTo(JsonStringOutput).strip() shouldBe expected.strip()
}

fun assert(init: Init<TestData>) {
    val testData = TestData().apply(init)
    val output = testData.query?.let { Query(it).interpret() } ?: return
    val expected = testData.expected?.strip() ?: return

    val actual = output.output().mapTo(JsonStringOutput).strip()
    actual shouldBe expected
}