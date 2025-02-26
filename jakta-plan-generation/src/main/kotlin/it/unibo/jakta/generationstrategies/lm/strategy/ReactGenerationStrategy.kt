package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.generation.PlanGenerationResult
import it.unibo.jakta.generationstrategies.lm.Expression
import it.unibo.jakta.generationstrategies.lm.Failure
import it.unibo.jakta.generationstrategies.lm.Goal
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.Precondition
import it.unibo.jakta.generationstrategies.lm.Stop
import it.unibo.jakta.generationstrategies.lm.pipeline.PromptGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.RequestGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.ResponseParser
import it.unibo.tuprolog.core.Tuple
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class ReactGenerationStrategy(
    override val genCfg: LMGenerationConfig,
    override var genState: LMGenerationState,
    override val promptGen: PromptGenerator,
    override val reqGen: RequestGenerator,
    override val responseParser: ResponseParser,
    override val logger: KLogger? = null,
) : LMGenerationStrategy {

    override fun copy(logger: KLogger?): GenerationStrategy =
        ReactGenerationStrategy(genCfg, genState, promptGen, reqGen, responseParser, logger)

    override fun requestPlanGeneration(
        generatedPlan: GeneratedPlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): PlanGenerationResult =
        runBlocking {
            coroutineScope {
                async {
                    if (!genState.startedGeneration) {
//                        val internalActions = context.internalActions.values.toList()
                        val externalActions = externalActions
                        val history = promptGen.buildPrompt(
                            emptyList(), // internalActions,
                            emptyList(), // externalActions,
                            context.beliefBase,
                            context.planLibrary,
                            generatedPlan,
                        )

                        genState = genState.copy(
                            startedGeneration = true,
                            history = genState.history + history,
                            context = context,
                            externalActions = externalActions,
                        )
                        println("--------------------------------------------------")
                        println("PROMPT")
                        println(history.first().content)
                        println("--------------------------------------------------")
                        println("GOAL")
                        println(history.last().content)
                        println("--------------------------------------------------")
//                        logger?.info { json.encodeToString(ListSerializer(ChatMessage.serializer()), genState.history) }
                    }

                    generate(generatedPlan)
                }
            }.await()
        }

    private suspend fun generate(generatedPlan: GeneratedPlan): PlanGenerationResult {
        val textCompletionRequest = makeRequest(genCfg, genState)
        val finishResult = reqGen.requestTextCompletion(logger, textCompletionRequest)
//        println()
//        logger?.info { json.encodeToString(ChatMessage.serializer(), finishResult.msg) }

        println(genState.history)
        genState = genState.copy(history = genState.history + finishResult.msg)

        return when (finishResult) {
            is Expression -> {
                // TODO log expression value to the user
                generate(generatedPlan)
            }
            is Stop -> {
                val completedPlan = Plan.of(
                    generatedPlan.trigger,
                    generatedPlan.guard,
                    generatedPlan.goals,
                )
                PlanGenerationResult(generatedPlan = completedPlan)
            }
            is Goal -> {
                val fragmentToParse = finishResult.fragmentToParse
                val newGoal = responseParser.parseGoal(fragmentToParse)
                if (newGoal != null) {
                    val generateGoal = Generate.of(newGoal)
                    val updatedPlan = GeneratedPlan.of(
                        generatedPlan.trigger,
                        generatedPlan.guard,
                        generatedPlan.goals.filterNot { it is EmptyGoal } + generateGoal,
                        generatedPlan.generationStrategy,
                    )
                    PlanGenerationResult(generatedPlan = updatedPlan)
                } else {
                    PlanGenerationResult(errorMsg = fragmentToParse)
                }
            }
            is Failure -> PlanGenerationResult(errorMsg = finishResult.msg.content.toString())
            is Precondition -> {
                val fragmentToParse = finishResult.fragmentToParse
                val newGuard = responseParser.parseStruct(fragmentToParse)
                if (newGuard != null) {
                    val updatedPlan = GeneratedPlan.of(
                        generatedPlan.trigger,
                        Tuple.wrapIfNeeded(generatedPlan.guard, newGuard).castToStruct(),
                        generatedPlan.goals,
                        generatedPlan.generationStrategy,
                    )
                    generate(updatedPlan)
                } else {
                    PlanGenerationResult(errorMsg = fragmentToParse)
                }
            }
        }
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

    override fun toString(): String {
        return """
            lmGenCfg=$genCfg
            promptGenerator=$promptGen
            requestGenerator=$reqGen
            responseParser=$responseParser
        """.trimIndent()
    }
}
