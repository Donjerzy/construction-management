/**
 * DictionaryReader class is responsible for reading word dictionaries and cleaning text for sentiment analysis.
 *
 * @property languageFileMap Mapping of language codes to their corresponding dictionary filenames.
 */
class DictionaryReader(private val languageFileMap: Map<String, String> = LANGUAGE_FILE_MAP) {

    /**
     * Reads dictionaries for the specified language and optionally includes emoticons and emojis.
     *
     * @param language The language for which the dictionaries are loaded.
     * @param emoticons Flag indicating whether to include emoticons and emojis.
     * @return A map containing words and their corresponding scores.
     * @throws IllegalArgumentException If the specified language is unknown.
     */
    fun readDictionaries(language: String, emoticons: Boolean): Map<String, String> {
        val filename = languageFileMap[language]
            ?: throw IllegalArgumentException("Unknown language: $language")

        val data = readWordFileFromResource(filename).toMutableMap()

        if (emoticons) {
            listOf("emoticons.txt", "emojis.txt").forEach { emoticon ->
                data += readWordFileFromResource(emoticon)
            }
        }
        return data
    }

    /**
     * Cleans the input text by removing punctuation and converting to lowercase.
     *
     * @param text The input text to be cleaned.
     * @return A list of cleaned words.
     */
    fun cleanText(text: String): List<String> {
        return text.replace("[!',.?]".toRegex(), "").lowercase().split(" ")
    }

    private fun readWordFileFromResource(filename: String?): Map<String, String> {
        val wordDict = mutableMapOf<String, String>()
        if (filename != null) {
            val resource = DictionaryReader::class.java.classLoader.getResourceAsStream(filename)
            resource?.bufferedReader()?.readLines()?.forEach { line ->
                val parts = line.trim().split("\t")
                if (parts.size == 2) {
                    wordDict[parts[0]] = parts[1]
                }
            }
        }
        return wordDict
    }
}
