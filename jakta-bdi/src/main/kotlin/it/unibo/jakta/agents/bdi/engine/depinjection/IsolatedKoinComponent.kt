package it.unibo.jakta.agents.bdi.engine.depinjection

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

internal interface IsolatedKoinComponent : KoinComponent {
    override fun getKoin(): Koin = JaktaKoin.koin
}
