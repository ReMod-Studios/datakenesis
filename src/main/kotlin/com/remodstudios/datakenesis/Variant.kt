package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable(with = VariantSerializer::class)
sealed interface Variant {

    @Serializable
    @DatakenesisDslMarker
    data class Simple(val model: Identifier,
                      val x: Int = 0,
                      val y: Int = 0,
                      val uvlock: Boolean = false): Variant

    @Serializable(with = Multi.AsSetSerializer::class)
    data class Multi(val models: Set<WeightedModel> = mutableSetOf()): Variant {
        internal object AsSetSerializer: KSerializer<Multi> {
            private val setSerializer = SetSerializer(WeightedModel.serializer())

            override val descriptor: SerialDescriptor = setSerializer.descriptor

            override fun deserialize(decoder: Decoder): Multi {
                val set = setSerializer.deserialize(decoder)
                return Multi(set.toMutableSet())
            }

            override fun serialize(encoder: Encoder, value: Multi) {
                setSerializer.serialize(encoder, value.models)
            }
        }
    }

    @DatakenesisDslMarker
    class MultiBuilder {
        val models: MutableSet<WeightedModel> = mutableSetOf()

        fun build() = Multi(models.toSet())

        fun add(model: WeightedModel) { models.add(model) }
        fun add(model: Identifier,
                x: Int = 0,
                y: Int = 0,
                uvlock: Boolean = false,
                weight: Int = 1) {
            models.add(WeightedModel(model, x, y, uvlock, weight))
        }
    }

    @Serializable
    @DatakenesisDslMarker
    data class WeightedModel(val model: Identifier,
                             val x: Int = 0,
                             val y: Int = 0,
                             val uvlock: Boolean = false,
                             val weight: Int = 1)
}

private object VariantSerializer : JsonContentPolymorphicSerializer<Variant>(Variant::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element.jsonObject["models"] is JsonArray -> Variant.Multi.serializer()
        else -> Variant.Simple.serializer()
    }
}

