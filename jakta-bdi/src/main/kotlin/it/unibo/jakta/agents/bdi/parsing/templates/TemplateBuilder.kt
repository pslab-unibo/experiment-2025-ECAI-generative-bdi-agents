package it.unibo.jakta.agents.bdi.parsing.templates

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStruct
import it.unibo.jakta.agents.bdi.parsing.templates.TemplatePart.Argument
import it.unibo.jakta.agents.bdi.parsing.templates.TemplatePart.Functor
import it.unibo.jakta.agents.bdi.parsing.templates.TemplatePart.Literal
import it.unibo.tuprolog.core.Struct
import kotlin.collections.forEach

class TemplateBuilder {
    fun parseTemplate(template: String): List<TemplatePart> {
        val pattern = Regex("""\[(.*?)]|\*(.*?)\*|([^\[*]+)""")
        val matches = pattern.findAll(template)

        return matches.map { match ->
            val (argument, functor, literal) = match.destructured
            when {
                argument.isNotEmpty() -> Argument(argument)
                functor.isNotEmpty() -> Functor(functor)
                literal.isNotEmpty() -> Literal(literal)
                else -> throw IllegalStateException("Invalid template pattern")
            }
        }.toList()
    }

    fun buildTemplateRegex(parts: List<TemplatePart>): Regex {
        val regexPattern = StringBuilder("^")

        parts.forEach { part ->
            when (part) {
                is Functor, is Literal -> {
                    regexPattern.append(part.text.replace(" ", "\\s"))
                }
                is Argument -> {
                    // Support quoted, bracketed, and plain word arguments
                    regexPattern.append("""(?:"(.*?)"|\[(\w+)]|(\w+))""")
                }
            }
        }

        regexPattern.append("$")
        return Regex(regexPattern.toString())
    }

    fun buildStruct(parts: List<TemplatePart>): Struct? {
        val functorName = mutableListOf<String>()
        val nameParts = mutableListOf<String>()
        val arguments = mutableListOf<String>()

        parts.forEach { part ->
            when (part) {
                is Functor -> functorName.add(part.text.lowercase())
                is Literal -> if (part.text.isNotBlank()) nameParts.add(part.text.lowercase())
                is Argument -> arguments.add(part.text)
            }
        }

        val functor = functorName.joinToString("_").ifEmpty { nameParts.joinToString("_") }
        val args = arguments.joinToString(", ") { it.capitalize() }

        val structAsString = StringBuilder().apply {
            append(functor)
            if (args.isNotEmpty()) { append("($args)") }
            append(".")
        }.toString()

        return tangleStruct(structAsString)
    }
}
