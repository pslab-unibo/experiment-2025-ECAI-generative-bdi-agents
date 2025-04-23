package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import com.charleskorn.kaml.YamlContentPolymorphicSerializer
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.yamlMap
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = TemplateSerializer::class)
sealed interface TemplateData {
    val purpose: String

    @Serializable
    @SerialName("goal")
    data class GoalTemplate(
        val goal: String,
        override val purpose: String,
    ) : TemplateData

    @Serializable
    @SerialName("belief")
    data class BeliefTemplate(
        val belief: String,
        override val purpose: String,
    ) : TemplateData
}

object TemplateSerializer : YamlContentPolymorphicSerializer<TemplateData>(TemplateData::class) {
    override fun selectDeserializer(node: YamlNode): DeserializationStrategy<TemplateData> = when {
        node.yamlMap.getKey("belief") != null -> TemplateData.BeliefTemplate.serializer()
        node.yamlMap.getKey("goal") != null -> TemplateData.GoalTemplate.serializer()
        else -> error("Unknown animal")
    }
}
