package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.PlanGenerationStepGoal
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.feedback.GenerationFeedback
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
import it.unibo.jakta.generationstrategies.lm.pipeline.formatter.FeedbackFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class ReactGenerationStrategy(
    override val generationConfig: LMGenerationConfig,
    override var generationState: LMGenerationState,
    override val promptGenerator: PromptGenerator,
    override val requestGenerator: RequestGenerator,
    override val responseParser: ResponseParser,
    override val feedbackFormatter: FeedbackFormatter,
    override val logger: KLogger? = null,
) : LMGenerationStrategy {
    override fun copy(logger: KLogger?): GenerationStrategy =
        ReactGenerationStrategy(
            generationConfig,
            generationState,
            promptGenerator,
            requestGenerator,
            responseParser,
            feedbackFormatter,
            logger,
        )

    override fun copy(
        generationConfig: LMGenerationConfig,
        generationState: LMGenerationState,
        promptGenerator: PromptGenerator,
        requestGenerator: RequestGenerator,
        responseParser: ResponseParser,
        feedbackFormatter: FeedbackFormatter,
        logger: KLogger?,
    ): LMGenerationStrategy =
        ReactGenerationStrategy(
            generationConfig,
            generationState,
            promptGenerator,
            requestGenerator,
            responseParser,
            feedbackFormatter,
            logger,
        )

    override fun requestPlanGeneration(
        generatedPlan: GeneratedPlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): PlanGenerationResult =
        runBlocking {
            coroutineScope {
                async {
                    if (!generationState.startedGeneration) {
                        val internalActions = context.internalActions.values.toList()
                        val externalActions = externalActions
                        val planLibrary = context.planLibrary.plans.filterNot { it is GeneratedPlan }
                        val history = promptGenerator.buildPrompt(
                            internalActions,
                            emptyList(), // externalActions,
                            context.beliefBase,
                            planLibrary,
                            generatedPlan,
                        )

                        generationState = generationState.copy(
                            startedGeneration = true,
                            history = generationState.history + history,
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
//                        logger?.info {
//                            json.encodeToString(
//                                ListSerializer(ChatMessage.serializer()),
//                                generationState.history,
//                            )
//                        }
                    }

                    generate(generatedPlan)
                }
            }.await()
        }

    override fun provideGenerationFeedback(generationFeedback: GenerationFeedback): GenerationStrategy {
        val history = generationState.history
        val chatMessageContent = feedbackFormatter.format(generationFeedback)
        return if (chatMessageContent.isNotBlank()) {
            val chatMessage = ChatMessage(role = ChatRole.User, content = chatMessageContent)
            println()
            println(chatMessage.content?.lines()?.joinToString("\n") { it.trimStart() })
            println()
            this.copy(generationState = generationState.copy(history = history + chatMessage))
        } else {
            this
        }
    }

    private suspend fun generate(generatedPlan: GeneratedPlan): PlanGenerationResult {
        val textCompletionRequest = makeRequest(generationConfig, generationState)
        val finishResult = requestGenerator.requestTextCompletion(logger, textCompletionRequest)
        println()
//        logger?.info { json.encodeToString(ChatMessage.serializer(), finishResult.msg) }

        generationState = generationState.copy(history = generationState.history + finishResult.msg)

        return when (finishResult) {
            is Expression, is Precondition -> generate(generatedPlan)
            is Stop -> {
                val completedPlan = Plan.of(
                    generatedPlan.id,
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
                    val planGoalGenerationStepGoal = PlanGenerationStepGoal.of(generatedPlan.id, newGoal)
                    val updatedPlan = GeneratedPlan.of(
                        generatedPlan.id,
                        generatedPlan.trigger,
                        generatedPlan.guard,
                        generatedPlan.goals.filterNot { it is EmptyGoal } + planGoalGenerationStepGoal,
                        this,
                        generatedPlan.literateTrigger,
                        generatedPlan.literateGuard,
                        generatedPlan.literateGoals,
                    )
                    PlanGenerationResult(generatedPlan = updatedPlan)
                } else {
                    PlanGenerationResult(errorMsg = fragmentToParse)
                }
            }
            is Failure -> {
                generationState = generationState.copy(
                    failedGenerations = generationState.failedGenerations + 1,
                )
                PlanGenerationResult(
                    trials = generationState.failedGenerations,
                    errorMsg = finishResult.msg.content.toString(),
                )
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
            lmGenCfg=$generationConfig
            promptGenerator=$promptGenerator
            requestGenerator=$requestGenerator
            responseParser=$responseParser
        """.trimIndent()
    }
}
