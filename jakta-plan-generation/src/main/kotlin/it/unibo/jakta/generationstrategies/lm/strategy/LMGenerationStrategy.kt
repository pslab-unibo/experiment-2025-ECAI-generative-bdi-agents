package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.LMGenScope
import it.unibo.jakta.generationstrategies.lm.configuration.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.configuration.LMInitialConfig
import it.unibo.jakta.generationstrategies.lm.configuration.LanguageModelConfig
import it.unibo.jakta.generationstrategies.lm.pipeline.feedback.FeedbackProvider
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.FeedbackFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMBinaryAnswerGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.BynaryResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ProgramParser
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.request.StreamProcessor
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.BinaryQuestionHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.TerminationStrategy
import kotlinx.serialization.json.Json

interface LMGenerationStrategy : GenerationStrategy {
    val promptBuilder: PromptBuilder
    val feedbackProvider: FeedbackProvider
    val terminationStrategy: TerminationStrategy

    override fun requestBlockingGeneration(
        generatingPlan: PartialPlan,
        generationState: GenerationState,
    ): GenerationResult

    override fun checkGenerationEnded(
        goal: Generate,
        generationState: GenerationState,
        beliefBase: BeliefBase,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState

    companion object {
        val json = Json { prettyPrint = true }

        fun multistep(
            generationCfg: LanguageModelConfig = LanguageModelConfig(),
        ): LMGenerationStrategyImpl =
            createStrategy(
                generationCfg.lmGenCfg,
                generationCfg.lmInitCfg,
            )

        fun multistep(generationCfg: LMGenScope.() -> Unit): LMGenerationStrategyImpl {
            val lmGenScopeCfg = LMGenScope().also(generationCfg).build()
            return createStrategy(
                lmGenScopeCfg.lmGenCfg,
                lmGenScopeCfg.lmInitCfg,
            )
        }

        private fun createStrategy(
            lmGenCfg: LMGenerationConfig,
            lmInitCfg: LMInitialConfig,
        ): LMGenerationStrategyImpl {
            val api = createOpenAIApi(lmInitCfg)
            val streamProcessor = StreamProcessor.of()
            val requestHandler = RequestHandler.of(api, streamProcessor, lmGenCfg)
            val feedbackFormatter = FeedbackFormatter.of()
            val feedbackProvider = FeedbackProvider.of(feedbackFormatter)
            val planGenerationResponseParser = ProgramParser.of()
            val planGenerator = LMPlanGenerator.multistep(requestHandler, planGenerationResponseParser)
            val answerParser = BynaryResponseParser.of()
            val answerGenerator = LMBinaryAnswerGenerator.of(requestHandler, answerParser)
            val questionHandler = BinaryQuestionHandler.of(answerGenerator)
            val promptFormatter = PromptFormatter.of()
            val promptBuilder = PromptBuilder.of(lmInitCfg.promptPath, lmInitCfg.remarks, promptFormatter)
            val terminationStrategy = TerminationStrategy.of(feedbackProvider, questionHandler)

            return LMGenerationStrategyImpl(
                planGenerator,
                promptBuilder,
                feedbackProvider,
                terminationStrategy,
            )
        }

        private fun createOpenAIApi(lmInitCfg: LMInitialConfig): OpenAI {
            val host = OpenAIHost(baseUrl = lmInitCfg.lmServerUrl)
            val config = OpenAIConfig(
                host = host,
                token = lmInitCfg.lmServerToken,
                logging = LoggingConfig(logger = Logger.Empty),
                timeout = Timeout(
                    request = lmInitCfg.requestTimeout,
                    connect = lmInitCfg.connectTimeout,
                    socket = lmInitCfg.socketTimeout,
                ),
            )
            return OpenAI(config)
        }

        fun KLogger.logChatMessage(msg: ChatMessage) {
            this.info {
                json.encodeToString(
                    ChatMessage.Companion.serializer(),
                    msg,
                )
            }
        }

        val configErrorMsg: (GenerationState) -> String = {
            "Expected a LMGenerationState but got ${it.javaClass.simpleName}"
        }
    }
}
