package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.actions.ActionRequest
import it.unibo.jakta.agents.bdi.engine.actions.ActionResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect
import it.unibo.tuprolog.core.Term
import kotlin.reflect.KFunction

abstract class AbstractActionsScope<C, Res, Req, A, As> :
    ScopeBuilder<Map<String, A>>
    where C : SideEffect,
          Res : ActionResponse<C>,
          Req : ActionRequest<C, Res>,
          A : Action<C, Res, Req>,
          As : ActionScope<C, Res, Req, A> {
    private val actions = mutableListOf<A>()

    fun action(
        name: String,
        vararg parameterNames: String,
        f: As.() -> Unit,
    ): Action<*, *, *> {
        val parameterNames = parameterNames.toList()
        return newAction(name, parameterNames, f = f).also {
            actions += it
        }
    }

    fun action(
        name: String,
        arity: Int = 0,
        f: As.() -> Unit,
    ) = newAction(name, arity, f = f).also { actions += it }

    fun action(method: KFunction<*>) =
        newAction(
            method.name,
            method.parameters.map { it.name!! },
        ) {
            method.call(*arguments.toTypedArray())
        }.also {
            actions += it
        }

    fun action(action: A) = action.also { actions += it }

    protected abstract fun newAction(
        name: String,
        arity: Int,
        purpose: String? = "$name/$arity",
        f: As.() -> Unit,
    ): A

    protected abstract fun newAction(
        name: String,
        parameterNames: List<String> = emptyList(),
        purpose: String? = "$name $parameterNames",
        f: As.() -> Unit,
    ): A

    override fun build(): Map<String, A> = actions.associateBy { it.signature.name }

    @Suppress("UNCHECKED_CAST")
    fun <T : Term> Req.argument(index: Int): T {
        // return ArgumentGetterDelegate(index)
        return this.arguments[index] as T
    }
}
