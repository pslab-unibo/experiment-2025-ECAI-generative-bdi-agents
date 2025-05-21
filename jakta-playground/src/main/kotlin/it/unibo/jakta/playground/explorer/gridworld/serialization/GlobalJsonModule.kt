package it.unibo.jakta.playground.explorer.gridworld.serialization

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(
    "it.unibo.jakta.agents.bdi.generationstrategies",
    "it.unibo.jakta.playground.explorer",
)
class GlobalJsonModule
