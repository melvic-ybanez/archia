import com.melvic.archia.output.JsonStringOutput
import com.melvic.archia.output.JsonValue
import com.melvic.archia.output.mapTo
import io.kotest.matchers.shouldBe

fun String.strip(): String {
    return trimIndent().replace(" ", "").replace("\n", "")
}

fun assert(result: JsonValue, expected: String) {
    result.mapTo(JsonStringOutput).strip() shouldBe expected.strip()
}