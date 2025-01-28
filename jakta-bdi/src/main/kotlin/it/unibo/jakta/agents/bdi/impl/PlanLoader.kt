package it.unibo.jakta.agents.bdi.impl

import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.llm.LLMConfiguration
import it.unibo.jakta.llm.output.CodeHandler
import java.util.Locale

class PlanLoader(
    cfg: LLMConfiguration,
) {
    val planClassName = "${cfg.taskName}PlanLibrary"
    val planSourcePath = "${cfg.planPaths.sourceWorkingDirectory}/$planClassName.kt"
    val planBytecodePath = cfg.planPaths.compilationWorkingDirectory // /${cfg.planPaths.packageDirectory}
    val loader = CodeHandler("$planBytecodePath/${cfg.planPaths.packageDirectory}/${cfg.goalName}")
    val planPackageName = cfg.planPaths.packageName
    val planClassQualifiedName = "$planPackageName.$planClassName"
    val planInternalFunctionName = cfg.goalName.decapitalize()

    fun loadPlan(planCode: String): Plan? {
        val planSourceClass = buildPlanClass(planCode)
        val error =
            loader.compileResponse(
                planSourceClass,
                planSourcePath,
                planBytecodePath,
            )

        return if (error == null) {
            loadPlanClass()
        } else {
            println(error)
            null
        }
    }

    private fun buildPlanClass(planCode: String): String =
        """
        |package $planPackageName
        |
        |import it.unibo.jakta.agents.bdi.actions.InternalAction
        |import it.unibo.jakta.agents.bdi.actions.InternalActions.Print
        |import it.unibo.jakta.agents.bdi.dsl.plans
        |import it.unibo.jakta.agents.bdi.dsl.plans.BodyScope
        |import it.unibo.jakta.agents.bdi.dsl.plans.PlansScope
        |import it.unibo.jakta.agents.bdi.impl.LLMPlan
        |
        |object $planClassName : LLMPlan {
        |    context (BodyScope)
        |    operator fun <T : InternalAction> T.invoke(
        |        arg: Any,
        |        vararg args: Any,
        |    ) {
        |        iact(signature.name(arg, *args))
        |    }
        |    
        |    override val plans =
        |        plans {
        |            $planInternalFunctionName()
        |        }
        |     
        |    fun PlansScope.$planInternalFunctionName() = 
        |        $planCode
        |}
        """.trimMargin("|")

    private fun loadPlanClass(): Plan? {
        val loadedObject = loader.loadObject<LLMPlan>(planClassQualifiedName)
        return loadedObject?.plans?.first()
    }

    companion object {
        fun String.decapitalize() = this.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
}
