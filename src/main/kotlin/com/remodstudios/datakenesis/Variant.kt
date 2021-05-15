package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable(with = Variant.Serializer::class)
sealed class Variant {

    @Serializable
    @DatakenesisDslMarker
    data class Simple(
        val model: Identifier,
        var x: Int = 0,
        var y: Int = 0,
        var uvlock: Boolean = false,
    ): Variant()

    @Serializable(with = Multi.AsSetSerializer::class)
    data class Multi(
        @SerialName("models")
        private var _models: MutableSet<WeightedModel> = mutableSetOf()
    ): Variant() {
        constructor(init: InitFor<Scope>) : this() { ScopeImpl().init() }

        val models: Set<WeightedModel> by this::_models

        @DatakenesisDslMarker
        interface Scope {
            fun model(model: WeightedModel)
            fun model(model: Identifier, init: InitFor<WeightedModel> = {}) {
                model(WeightedModel(model).apply(init))
            }
        }

        @DatakenesisDslMarker
        private inner class ScopeImpl: Scope {
            override fun model(model: WeightedModel) { _models.add(model) }
        }

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

    internal object Serializer : JsonContentPolymorphicSerializer<Variant>(Variant::class) {
        override fun selectDeserializer(element: JsonElement) = when {
            element.jsonObject["models"] is JsonArray -> Multi.serializer()
            else -> Simple.serializer()
        }
    }

    @Serializable
    @DatakenesisDslMarker
    data class WeightedModel(
        val model: Identifier,
        var x: Int = 0,
        var y: Int = 0,
        var uvlock: Boolean = false,
        var weight: Int = 1
    )
}

