import com.melvic.archia.ast.*
import com.melvic.archia.interpreter.*
import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.mapTo
import io.kotest.matchers.shouldBe

data class TestData(
    var query: Clause? = null,
    var expected: String? = null,
    var errors: List<ErrorCode> = listOf()
) : BuilderHelper {
    fun query(init: Init<ClauseBuilder>) {
        setClause(init) { query = it }
    }
}

fun String.trimWhitespace(): String {
    return trimIndent().replace(" ", "").replace("\n", "")
}

fun assert(result: JsonValue, expected: String) {
    result.mapTo(JsonStringOutput).trimWhitespace() shouldBe expected.trimWhitespace()
}

fun assert(init: Init<TestData>) {
    val testData = TestData().apply(init)
    val output = testData.query?.let { Query(it).interpret() } ?: return
    val expected = testData.expected?.trimWhitespace() ?: return

    val actual = output.output().mapTo(JsonStringOutput).trimWhitespace()
    actual shouldBe expected
}

fun assertFail(init: Init<TestData>) {
    val testData = TestData().apply(init)
    val output = testData.query?.let { Query(it).interpret() } ?: return
    output shouldBe Failed(testData.errors)
}