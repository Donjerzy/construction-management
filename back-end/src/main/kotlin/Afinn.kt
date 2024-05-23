/**
 * Afinn class represents a sentiment analysis tool based on the AFINN-111 wordlist.
 *
 * @property language The language for which the sentiment analysis is performed. Defaults to English ("en").
 * @property emoticons A flag indicating whether to include emoticons and emojis in sentiment analysis. Defaults to false.
 */
class Afinn(private val language: String = "en", private val emoticons: Boolean = false) {
    private var wordDict: Map<String, String>
    private val dictionaryReader = DictionaryReader()

    /**
     * Initializes the Afinn instance by reading and loading the necessary word dictionaries.
     */
    init {
        wordDict = dictionaryReader.readDictionaries(language, emoticons)
    }
    /**
     * Calculates the sentiment score for the given text based on the loaded word dictionaries.
     *
     * @param text The input text for sentiment analysis.
     * @return The sentiment score for the text.
     */
    fun score(text: String): Double {
        val cleanedText = dictionaryReader.cleanText(text)
        return cleanedText.sumOf { word: String ->
            wordDict.getOrDefault(word, "0").toDouble()
        }
    }

    /**
     * Determines the sentiment category (positive, neutral, or negative) based on the sentiment score.
     *
     * @param text The input text for sentiment analysis.
     * @return A string representing the sentiment category.
     */
    fun scoreToWords(text: String): String {
        val scored = score(text)
        return when {
            scored in -1.0..1.0 -> "neutral"
            scored > 1.0 -> "positive"
            else -> "negative"
        }
    }
}
