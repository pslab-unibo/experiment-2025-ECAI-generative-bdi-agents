package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.generation.PlanGenerationResult
import it.unibo.jakta.generationstrategies.lm.LMGenScope
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.PromptGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.PromptGeneratorImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.RequestGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.RequestGeneratorImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.ResponseParserImpl

interface LMGenerationStrategy : GenerationStrategy {
    override val lmGenCfg: LMGenerationConfig
    val genState: LMGenerationState
    val promptGen: PromptGenerator
    val reqGen: RequestGenerator
    val responseParser: ResponseParser

    override fun requestPlanGeneration(generatedPlan: GeneratedPlan): PlanGenerationResult

    override fun parseResponse(
        logger: KLogger,
        response: String,
    ): PlanGenerationResult

    companion object {
        fun react(generationCfg: LMGenScope.() -> Unit): ReactGenerationStrategy {
            val lmGenScopeCfg = LMGenScope().also(generationCfg).build()
            val lmInitCfg = lmGenScopeCfg.lmInitCfg
            val host = OpenAIHost(baseUrl = lmInitCfg.lmServerUrl)
            val config = OpenAIConfig(host = host, token = lmInitCfg.lmServerToken)
            val api = OpenAI(config)

            return ReactGenerationStrategy(
                lmGenScopeCfg.lmGenCfg,
                LMGenerationState(),
                PromptGeneratorImpl(lmInitCfg.remarks),
                RequestGeneratorImpl(api),
                ResponseParserImpl(),
            )
        }
    }
}
