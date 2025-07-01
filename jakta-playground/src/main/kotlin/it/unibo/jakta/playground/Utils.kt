package it.unibo.jakta.playground

import java.io.File
import java.util.Properties

object Utils {
    /**
     * Determines the appropriate indefinite article ("a" or "an") for a given word
     * based on pronunciation rules rather than just spelling.
     *
     * @param word The word for which to determine the appropriate indefinite article
     * @return "a" or "an" based on the word's pronunciation
     */
    fun getIndefiniteArticle(word: String): String {
        if (word.isBlank()) return "a"

        val w = word.trim().lowercase()
        if (w.isEmpty()) return "a"

        // Words that start with a vowel letter but have a consonant sound
        val usesAPrefix =
            listOf(
                "eu",
                "ewe",
                "u",
                "uni",
                "use",
                "user",
                "util",
                "usu",
                "ubiq",
                "uk",
                "one",
                "once",
                "ou",
                "y",
            )

        // Words that start with a consonant letter but have a vowel sound
        val usesAnPrefix =
            listOf(
                "hour",
                "heir",
                "hon",
                "honest",
                "herb",
                "x",
            )

        return when {
            // Special case for abbreviations and acronyms
            w.length == 1 -> {
                // Single letters follow pronunciation rules
                val vowelSoundLetters = listOf('a', 'e', 'f', 'h', 'i', 'l', 'm', 'n', 'o', 'r', 's', 'x')
                if (w[0] in vowelSoundLetters) "an" else "a"
            }
            // Check for exceptions where words start with vowel but use "a"
            usesAPrefix.any { w.startsWith(it) } -> "a"
            // Check for exceptions where words start with consonant but use "an"
            usesAnPrefix.any { w.startsWith(it) } -> "an"
            // Standard vowel check
            w[0] in listOf('a', 'e', 'i', 'o', 'u') -> "an"
            // Default to "a" for consonant sounds
            else -> "a"
        }
    }

    fun readTokenFromEnv(): String {
        val envFile = File(".env")
        if (!envFile.exists()) {
            throw IllegalStateException(".env file not found")
        }

        val props = Properties()
        envFile.inputStream().use { props.load(it) }

        return props.getProperty("API_KEY")
            ?: throw IllegalStateException("API_KEY not found in .env file")
    }
}
