fun String.strip(): String {
    return trimIndent().replace(" ", "").replace("\n", "")
}