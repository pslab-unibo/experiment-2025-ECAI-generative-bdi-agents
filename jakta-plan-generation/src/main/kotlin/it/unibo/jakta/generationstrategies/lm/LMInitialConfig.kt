package it.unibo.jakta.generationstrategies.lm

import kotlin.time.Duration

data class LMInitialConfig(
    val lmServerUrl: String,
    val lmServerToken: String,
    val remarks: List<Remark> = emptyList(),
    val requestTimeout: Duration,
    val connectTimeout: Duration,
    val socketTimeout: Duration,
)
