package it.unibo.jakta.playground.evaluation.plandata

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.PGPSuccess
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationRequested
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageReceived
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageSent
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.AdmissibleBeliefParseFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.AdmissibleGoalParseFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserSuccess
import it.unibo.jakta.playground.evaluation.FileProcessor.processFile
import it.unibo.jakta.playground.gridworld.logging.ObjectReachedEvent
import java.io.File

data class LMPGPInvocation(
    val pgpId: String,
    val history: List<ChatMessage>,
    val rawMessageContents: List<String>,
    val generatedPlans: List<Plan> = emptyList(),
    val generatedAdmissibleGoals: List<AdmissibleGoal> = emptyList(),
    val generatedAdmissibleBeliefs: List<AdmissibleBelief> = emptyList(),
    val plansNotParsed: Int = 0,
    val admissibleGoalsNotParsed: Int = 0,
    val admissibleBeliefNotParsed: Int = 0,
    val timeUntilCompletion: Long? = 0,
    val executable: Boolean = true,
    val reachesDestination: Boolean = true,
    val generationConfig: LMGenerationConfig? = null,
    val chatCompletionId: String? = null,
) {
    companion object {
        fun from(
            pgpId: String,
            agentLogFile: File,
            pgpLogFile: File,
        ): LMPGPInvocation {
            val history = mutableListOf<ChatMessage>()
            val rawMessageContents = mutableListOf<String>()
            var genCfg: LMGenerationConfig? = null
            var chatCompletionId: String? = null
            processFile(pgpLogFile) { logEntry ->
                val event = logEntry.message.event
                when (event) {
                    is LMMessageReceived -> {
                        chatCompletionId = event.chatCompletionId
                        event.chatMessage.content?.let { rawMessageContents.add(it) }
                        event.chatMessage.let { history.add(it) }
                    }

                    is LMMessageSent -> event.chatMessage.let { history.add(it) }

                    is LMGenerationRequested -> {
                        event.genConfig.let { genCfg = it }
                    }
                }
                true
            }

            var plansNotParsed = 0
            var admissibleGoalsNotParsed = 0
            var admissibleBeliefNotParsed = 0
            val parser = Parser.create()
            rawMessageContents.forEach {
                val result = parser.parse(it)
                when (result) {
                    is ParserSuccess.NewResult -> {
                        if (result.parsingErrors.isNotEmpty()) {
                            plansNotParsed++
                            result.parsingErrors.forEach { error ->
                                when (error) {
                                    is AdmissibleBeliefParseFailure -> admissibleGoalsNotParsed++
                                    is AdmissibleGoalParseFailure -> admissibleBeliefNotParsed++
                                    else -> {}
                                }
                            }
                        }
                    }
                    else -> plansNotParsed++
                }
            }

            var reachesDestination = false
            var timeOfCompletion: Long? = 0L
            val generatedPlans = mutableListOf<Plan>()
            val generatedAdmissibleGoals = mutableListOf<AdmissibleGoal>()
            val generatedAdmissibleBeliefs = mutableListOf<AdmissibleBelief>()
            processFile(agentLogFile) { logEntry ->
                val event = logEntry.message.event
                when (val ev = event) {
                    is PGPSuccess.GenerationCompleted -> {
                        if (ev.pgpId.id == pgpId) {
                            generatedPlans.addAll(ev.plans)
                            generatedAdmissibleGoals.addAll(ev.admissibleGoals)
                            generatedAdmissibleBeliefs.addAll(ev.admissibleBeliefs)
                        }
                    }
                    is ObjectReachedEvent -> {
                        if (ev.objectName == "home") {
                            reachesDestination = true
                            timeOfCompletion = logEntry.message.cycleCount
                        }
                    }
                }
                true
            }

            return LMPGPInvocation(
                pgpId = pgpId,
                history = history,
                rawMessageContents = rawMessageContents,
                generatedPlans = generatedPlans,
                generatedAdmissibleGoals = generatedAdmissibleGoals,
                generatedAdmissibleBeliefs = generatedAdmissibleBeliefs,
                plansNotParsed = plansNotParsed,
                admissibleGoalsNotParsed = admissibleGoalsNotParsed,
                admissibleBeliefNotParsed = admissibleBeliefNotParsed,
                timeUntilCompletion = timeOfCompletion,
                executable = isExecutable(generatedPlans),
                reachesDestination = reachesDestination,
                generationConfig = genCfg,
                chatCompletionId = chatCompletionId,
            )
        }

        // The PGP is considered executable if there is at least a parsed plan
        fun isExecutable(generatedPlans: List<Plan>) = generatedPlans.isNotEmpty()
    }
}
