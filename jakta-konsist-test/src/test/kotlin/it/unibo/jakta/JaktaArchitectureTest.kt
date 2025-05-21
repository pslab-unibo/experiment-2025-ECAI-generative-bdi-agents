package it.unibo.jakta

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.FreeSpec

class JaktaArchitectureTest :
    FreeSpec({
        "architecture layers have correct dependencies" {
            projectScope
                .assertArchitecture {
                    val bdiEngineLayer = Layer("BDI Engine", "it.unibo.jakta.agents.bdi.engine..")
                    val dslLayer = Layer("DSL", "it.unibo.jakta.agents.bdi.dsl..")
                    val planGenLayer = Layer("Plan Generation", "it.unibo.jakta.agents.bdi.generationstrategies..")
                    val fsmLayer = Layer("Finite State Machine", "it.unibo.jakta.agents.fsm..")

                    dslLayer.dependsOn(bdiEngineLayer)
                    planGenLayer.dependsOn(bdiEngineLayer)
                    bdiEngineLayer.dependsOn(fsmLayer)

                    bdiEngineLayer.doesNotDependOn(dslLayer)
                    bdiEngineLayer.doesNotDependOn(planGenLayer)

                    dslLayer.doesNotDependOn(planGenLayer)

                    fsmLayer.dependsOnNothing()
                }
        }
    }) {
    companion object {
        val projectScope = Konsist.scopeFromProduction()
    }
}
