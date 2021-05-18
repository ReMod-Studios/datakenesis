package com.remodstudios.datakenesis

import kotlinx.serialization.Serializable

/**
 * Represents a block state definition JSON file, usually found in the `assets/<namespace>/blockstates/` folder of a
 * resource pack. The file is used for blocks to search for models corresponding to the block's state.
 */
@Serializable
data class BlockState(val variants: Map<String, Variant>)

class BlockStateBuilder {
    val variants: MutableMap<String, Variant> = mutableMapOf()

    fun build() = BlockState(variants.toMap())

    /**
     * Adds a new variant.
     * @param state the corresponding state string
     * @param variant the variant
     */
    fun variant(state: String, variant: Variant) { variants[state] = variant }

    /**
     * Adds a new variant with a single model.
     * @param state the corresponding state string
     * @param model the model ID of the model
     * @param init a block used to further modify the model (optional)
     */
    fun variant(state: String,
                model: Identifier,
                x: Int = 0,
                y: Int = 0,
                uvlock: Boolean = false) {
        variants[state] = Variant.Simple(model, x, y, uvlock)
    }

    /**
     * Adds a new multi-variant, which chooses a model randomly from its pool.
     * @param state the corresponding state string
     * @param init a block that initializes and modifies the multi-variant
     */
    inline fun multiVariant(state: String, init: InitFor<Variant.MultiBuilder>) {
        variant(state, Variant.MultiBuilder().apply(init).build())
    }

    /**
     * Shortcut of `variant` with an empty state string.
     * This is useful for blocks without properties.
     * @param variant the variant
     */
    fun stateless(variant: Variant) { variants[""] = variant }
    /**
     * Shortcut of `variant` with an empty state string.
     * This is useful for blocks without properties.
     * @param model the model ID of the model
     * @param init a block used to further modify the model (optional)
     */
    fun stateless(model: Identifier,
                  x: Int = 0,
                  y: Int = 0,
                  uvlock: Boolean = false) {
        variants[""] = Variant.Simple(model, x, y, uvlock)
    }
    /**
     * Shortcut of `multiVariant` with an empty state string.
     * This is useful for blocks without properties.
     * @param init a block that initializes and modifies the multi-variant
     */
    inline fun statelessMulti(init: InitFor<Variant.MultiBuilder>) {
        variants[""] = Variant.MultiBuilder().apply(init).build()
    }
}

fun blockState(init: InitFor<BlockStateBuilder>) = BlockStateBuilder().apply(init).build()
