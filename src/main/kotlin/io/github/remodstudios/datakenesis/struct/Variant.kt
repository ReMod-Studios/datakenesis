package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.Serializable

@Serializable(with = VariantSerializer::class)
sealed interface Variant {

    @Serializable
    @DatakenesisDslMarker
    data class Simple(val model: Identifier,
                      val x: Int = 0,
                      val y: Int = 0,
                      val uvlock: Boolean = false): Variant

    @Serializable(with = VariantMultiAsSetSerializer::class)
    data class Multi(val models: Set<WeightedModel>): Variant

    @DatakenesisDslMarker
    class MultiBuilder {
        val models: MutableSet<WeightedModel> = mutableSetOf()

        fun build() = Multi(models)

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


