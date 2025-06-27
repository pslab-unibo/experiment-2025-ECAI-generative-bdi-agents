package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.PromptBuilder

enum class PromptType(
    val builder: PromptBuilder,
) {
    PROMPT_WITH_HINTS(DefaultPromptBuilder.promptWithHints),
    PROMPT_WITHOUT_HINTS(DefaultPromptBuilder.promptWithoutHints),
}
