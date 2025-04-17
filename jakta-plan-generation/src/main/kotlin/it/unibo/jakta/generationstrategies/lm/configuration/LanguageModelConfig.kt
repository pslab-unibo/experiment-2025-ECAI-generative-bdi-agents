package it.unibo.jakta.generationstrategies.lm.configuration

data class LanguageModelConfig(
    val lmInitCfg: LMInitialConfig = LMInitialConfig(),
    val lmGenCfg: LMGenerationConfig = LMGenerationConfig(),
)
