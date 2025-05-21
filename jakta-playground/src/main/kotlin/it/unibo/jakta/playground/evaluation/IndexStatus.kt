package it.unibo.jakta.playground.evaluation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IndexStatus(
    val health: String,
    val status: String,
    val index: String,
    val uuid: String,
    val pri: String,
    val rep: String,
    @SerialName("docs.count")
    val docsCount: String,
    @SerialName("docs.deleted")
    val docsDeleted: String,
    @SerialName("store.size")
    val storeSize: String,
    @SerialName("pri.store.size")
    val priStoreSize: String,
)
