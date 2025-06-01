package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.dsl.actions.ExternalActionScope
import it.unibo.jakta.agents.bdi.dsl.actions.ExternalActionsScope
import it.unibo.jakta.agents.bdi.dsl.actions.InternalActionScope
import it.unibo.jakta.agents.bdi.dsl.actions.InternalActionsScope
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefsScope
import it.unibo.jakta.agents.bdi.dsl.environment.EnvironmentScope
import it.unibo.jakta.agents.bdi.dsl.logging.LoggingConfigScope
import it.unibo.jakta.agents.bdi.dsl.plans.PlansScope
import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

@DslMarker
annotation class JaktaDSL

@JaktaDSL
fun mas(f: MasScope.() -> Unit): Mas = MasScope().also(f).build()

fun internalAction(
    name: String,
    arity: Int,
    f: InternalActionScope.() -> Unit,
) = InternalActionsScope().newAction(name, arity, emptyList(), f = f)

fun externalAction(
    name: String,
    arity: Int,
    f: ExternalActionScope.() -> Unit,
) = ExternalActionsScope().newAction(name, arity, emptyList(), f = f)

@JaktaDSL
fun environment(f: EnvironmentScope.() -> Unit): Environment = EnvironmentScope().also(f).build()

@JaktaDSL
fun agent(
    name: String,
    masID: MasID = MasID(),
    f: AgentScope.() -> Unit,
): Agent = AgentScope(masID, name).also(f).build()

fun plans(f: PlansScope.() -> Unit): Iterable<Plan> = PlansScope().also(f).build()

fun beliefs(f: BeliefsScope.() -> Unit) = BeliefsScope().also(f).build()

fun loggingConfig(block: LoggingConfigScope.() -> Unit): LoggingConfig = LoggingConfigScope().also(block).build()

operator fun String.invoke(vararg terms: Term): Struct = Struct.of(this, *terms)

operator fun String.invoke(terms: List<Term>): Struct = Struct.of(this, terms)
