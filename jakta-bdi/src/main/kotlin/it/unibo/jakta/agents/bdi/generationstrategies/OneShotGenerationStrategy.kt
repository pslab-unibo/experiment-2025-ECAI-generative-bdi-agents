package it.unibo.jakta.agents.bdi.generationstrategies

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.generationstrategies.GenerationStrategy.Companion.loadTheoryFromString
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.generated.GeneratedPlan
import it.unibo.jakta.llm.LLMCaller
import it.unibo.jakta.llm.PromptGenerator
import it.unibo.tuprolog.core.parsing.ParseException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class OneShotGenerationStrategy(
    override val actions: List<Action<*, *, *>> = emptyList(),
    override val planLibrary: PlanLibrary? = null,
) : GenerationStrategy {

    override fun generatePlan(plan: GeneratedPlan): PlanGenerationResult? = runBlocking {
        coroutineScope { async { requestPlanGeneration(plan) } }.await()
    }

    private suspend fun requestPlanGeneration(plan: GeneratedPlan): PlanGenerationResult? {
        val genCfg = plan.genCfg
        val availableActions = genCfg.actions
        val acts = if (availableActions.isEmpty()) {
            actions
                .filter { it.signature.description.isNotBlank() }
                .map { it.signature.toPrologDoc() }
                .toList()
        } else {
            availableActions
        }

        val givenRemarks = genCfg.remarks
        val remarks = if (givenRemarks.isEmpty()) {
            "None."
        } else {
            givenRemarks.joinToString("\n") { it.remark }
        }

        val prompt = PromptGenerator(
            actions = acts.joinToString("\n\n"),
            remarks = remarks,
            goal = """
            | ${plan.trigger.value.functor}
            | The plan should work for any value of ${plan.trigger.value.args.joinToString(
                " and ",
            ) { it.castToVar().name }}.
            """.trimMargin("| "),
        ).buildPrompt()

        val message = LLMCaller().callLLM(prompt)
        return if (message.step != null) {
            try {
//                println(actions.joinToString("\n") { it.signature.name })
//                println(planLibrary?.plans?.map { it.trigger.value }?.joinToString("\n"))
                // TODO what if the output is not an action?
                val goals = loadTheoryFromString(message.step!!.planBody).rules.map { Act.of(it.head) }
//                goals.forEach { println(it) }
                // TODO handle guards
                val generatedPlan = GeneratedPlan.of(plan.trigger, plan.guard, genCfg, goals)
                PlanGenerationResult(generatedPlan)
            } catch (e: ParseException) {
                PlanGenerationResult(errorMsg = e.message)
            }
        } else {
            PlanGenerationResult(errorMsg = message.errorMsg)
        }
    }

    override fun copy(
        plans: PlanLibrary?,
        actions: List<Action<*, *, *>>,
    ): GenerationStrategy = OneShotGenerationStrategy(actions, plans)
}
