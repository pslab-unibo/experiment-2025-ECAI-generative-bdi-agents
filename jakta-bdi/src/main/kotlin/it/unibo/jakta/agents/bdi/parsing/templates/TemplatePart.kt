package it.unibo.jakta.agents.bdi.parsing.templates

sealed interface TemplatePart {
    val text: String
    data class Literal(override val text: String) : TemplatePart
    data class Functor(override val text: String) : TemplatePart
    data class Argument(override val text: String) : TemplatePart
}
