package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.Prolog2Jakta
import it.unibo.jakta.agents.bdi.visitors.SourceWrapperVisitor
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.JaktaParser.tangleStruct
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.PlanData
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlin.text.replace

object LiterateGuardParser {
    private fun wrapBelief(struct: Struct): Struct =
        SourceWrapperVisitor.wrapSelectively(struct).castToStruct()

    fun processGuard(plan: PlanData): Struct? {
        val individualConditions = plan.conditions
            .mapNotNull { c ->
                val text = c.replace("¬", "~")
                    .replace("!=", "\\=")
                    .replace(Regex("\\bnot\\b"), "~")
                    .replace("true", "True")
                    .replace("false", "False")
                    .replace(Regex("\\bor\\b"), "|")
                    .replace(Regex("\\band\\b"), "&")

                if (c.contains("<none>")) {
                    Truth.TRUE
                } else {
                    tangleStruct(text)?.accept(Prolog2Jakta)?.castToStruct()
                }
            }
            .map { if (it != Truth.TRUE) wrapBelief(it) else it }

        return individualConditions.toLeftNestedAnd()
    }
}
