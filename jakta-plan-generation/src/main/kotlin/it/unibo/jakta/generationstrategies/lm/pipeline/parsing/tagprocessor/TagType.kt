package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor

enum class TagType {
    Goals, Predicates, Plan, Step;

    companion object {
        fun fromString(str: String): TagType? =
            try {
                TagType.valueOf(str)
            } catch (_: IllegalArgumentException) {
                null
            }
    }
}
