package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.generation.PlanGenerationResult
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.PromptGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.RequestGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.ResponseParser
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class ReactGenerationStrategy(
    override val lmGenCfg: LMGenerationConfig,
    override val genState: LMGenerationState,
    override val promptGen: PromptGenerator,
    override val reqGen: RequestGenerator,
    override val responseParser: ResponseParser,
) : LMGenerationStrategy {

    override fun requestPlanGeneration(generatedPlan: GeneratedPlan): PlanGenerationResult =
        runBlocking { coroutineScope { async { generate(generatedPlan) } }.await() }

    private suspend fun generate(generatedPlan: GeneratedPlan): PlanGenerationResult {
        if (!genState.startedGeneration) {
            val internalActions = genState.context?.internalActions?.values?.toList()
            val externalActions = genState.externalActions
            val history = promptGen.buildPrompt(
                internalActions,
                externalActions,
                genState.context?.beliefBase,
                genState.context?.planLibrary,
                generatedPlan.trigger.value.functor,
            )

            genState.history += history
        }

        // TODO handle conversation state and multiple conversations from a generated plan
        val finishResult = reqGen.requestTextCompletion(
            genState.logger,
            makeRequest(lmGenCfg, genState),
        )
        // TODO handle generation of plan
        return PlanGenerationResult(errorMsg = finishResult.value)
    }

    private fun makeRequest(cfg: LMGenerationConfig, state: LMGenerationState) =
        ChatCompletionRequest(
            model = ModelId(cfg.modelId),
            temperature = cfg.temperature,
            messages = state.history,
            maxTokens = cfg.maxTokens,
//            logprobs = true,
//            topLogprobs = 5,
        )

    override fun parseResponse(logger: KLogger, response: String): PlanGenerationResult =
        responseParser.parse(logger, response)

    override fun toString(): String {
        return """
            lmGenCfg=$lmGenCfg
            promptGenerator=$promptGen
            requestGenerator=$reqGen
            responseParser=$responseParser
        """.trimIndent()
    }
}
