package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.impl

import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.AgentContextProperties
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.PromptScope
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.SystemPromptBuilder

internal class SystemPromptBuilderImpl(
    messageName: String,
    block: PromptScope.(AgentContextProperties) -> Unit,
) : SystemPromptBuilder,
    PromptBuilder by PromptBuilderImpl(
        messageName = messageName,
        role = ChatRole.System,
        block = block,
    ) {
    companion object {
        fun system(
            messageName: String,
            block: PromptScope.(AgentContextProperties) -> Unit,
        ): SystemPromptBuilder = SystemPromptBuilderImpl(messageName, block)
    }
}
