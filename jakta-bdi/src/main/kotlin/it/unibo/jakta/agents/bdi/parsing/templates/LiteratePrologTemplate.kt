package it.unibo.jakta.agents.bdi.parsing.templates

import it.unibo.tuprolog.core.Struct

interface LiteratePrologTemplate {
    val template: String
    val templateBuilder: TemplateBuilder
    val parsedTemplate: List<TemplatePart> get() = templateBuilder.parseTemplate(template)
    val struct: Struct? get() = templateBuilder.buildStruct(parsedTemplate)
    val regex: Regex get() = templateBuilder.buildTemplateRegex(parsedTemplate)

    fun matches(input: String): Boolean = regex.matches(input)

    /**
     * Uses a list of pairs since slots are ordered and can have the same name.
     */
    fun extractSlotValues(input: String): List<Pair<String, String>> {
        val matchResult = regex.matchEntire(input) ?: return emptyList()
        val argumentParts = parsedTemplate.filterIsInstance<TemplatePart.Argument>()

        var groupIndex = 1
        val results = mutableListOf<Pair<String, String>>()

        argumentParts.forEach { arg ->
            val quotedValue = matchResult.groupValues.getOrNull(groupIndex)?.takeIf { it.isNotEmpty() }
            val bracketedValue = matchResult.groupValues.getOrNull(groupIndex + 1)?.takeIf { it.isNotEmpty() }
            val plainValue = matchResult.groupValues.getOrNull(groupIndex + 2)?.takeIf { it.isNotEmpty() }

            val value = quotedValue ?: bracketedValue ?: plainValue

            if (value != null) {
                val formatted = when {
                    quotedValue != null -> value
                    bracketedValue != null -> value.replaceFirstChar { it.uppercaseChar() }
                    else -> value.lowercase()
                }
                results.add(arg.text to formatted)
            } else {
                results.add(arg.text to arg.text)
            }

            groupIndex += 3
        }

        return results
    }

    fun buildGroundTerm(
        orderedArgs: List<Pair<String, String>>,
        stopWords: Set<String> = defaultStopWords,
    ): String {
        val args = orderedArgs.map { (_, value) -> value }
        val functor = parsedTemplate
            .filterIsInstance<TemplatePart.Functor>()
            .joinToString("_") { processLiteral(it.text, stopWords = emptySet()) }
            .ifEmpty {
                parsedTemplate
                    .filterIsInstance<TemplatePart.Literal>()
                    .map { processLiteral(it.text, stopWords) }
                    .filter { it.isNotBlank() }
                    .joinToString("_")
            }

        return "$functor(${args.joinToString(", ")})."
    }

    private fun processLiteral(
        text: String,
        stopWords: Set<String> = defaultStopWords,
    ): String =
        text.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .filter { it !in stopWords }
            .joinToString("_")

    companion object {
        val defaultStopWords = setOf(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with",
        )
    }
}
