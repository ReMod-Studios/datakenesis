package com.remodstudios.datakenesis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a block state definition JSON file, usually found in the `assets/<namespace>/blockstates/` folder of a
 * resource pack. The file is used for blocks to search for models corresponding to the block's state.
 *
 * Usage example:
 * ```kt
 * val example = BlockState {                            // The scope where all of the functions are in effect
 *
 *     stateless("haha".asId) {                             // `stateless` is syntactic sugar for a state with no
 *                                                          // properties (having "" as key). It takes an `Identifier`,
 *                                                          // which can be easily constructed via `asId`, and a code
 *                                                          // block that can set the properties of the model.
 *
 *         x = 90                                           // Sets the X rotation of the model: note that vanilla
 *                                                          // clients reject angles other than multiples of 90.
 *                                                          // Y rotation works similarly, with the property `y`.
 *
 *         uvlock = true                                    // Locks the UV in place. More info:
 *                                                          // https://minecraft.fandom.com/wiki/Model#Block_states
 *     }
 *     variant("facing=north", "haha_north".asId) {         // `variant` takes a state string in the form of
 *                                                          // property1=value1,(property2=value2,)...(propertyN=valueN)
 *                                                          // along with the model ID.
 *         x = 180
 *     }
 *     multiVariant("facing=south") {                       // `multiVariant` makes the game to choose one model randomly.
 *                                                          // This can be used to add variety to your models, especially
 *                                                          // with repetitive and common blocks like dirt, grass blocks,
 *                                                          // cobblestone and netherrack.
 *
 *         model("haha_normal".asId) {                      // Defines the first model
 *             x = 90
 *             y = 270
 *             weight = 100                                 // Weight determines how likely a model would be chosen.
 *                                                          // The probability is equal to this model's weight divided by
 *                                                          // the sum of all models' weights. In this case, the
 *                                                          // probability is 100/(100+2), or around 98.0%.
 *         }
 *         model("haha_rare".asId) {
 *             x = 90
 *             y = 90
 *             weight = 2                                   // 2/(100+2), around 2.0%
 *         }
 *     }
 *
 * }
 * ```
 */
@Serializable
data class BlockState(
    @SerialName("variants")
    private val _variants: MutableMap<String, Variant> = mutableMapOf()
)
{
    constructor(init: InitFor<Scope>): this() { ScopeImpl().init() }

    /**
     * A read-only interface to get all associated variants.
     */
    val variants: Map<String, Variant> by this::_variants

    @DatakenesisDslMarker
    interface Scope {
        /**
         * Adds a new variant with a single model.
         * @param state the corresponding state string
         * @param model the model
         */
        fun variant(state: String, model: Variant.Simple)

        /**
         * Adds a new variant with a single model.
         * @param state the corresponding state string
         * @param model the model ID of the model
         * @param init a block used to further modify the model (optional)
         */
        fun variant(state: String, model: Identifier, init: InitFor<Variant.Simple> = {}) {
            variant(state, Variant.Simple(model).apply(init))
        }

        /**
         * Adds a new multi-variant, which chooses a model randomly from its pool.
         * @param state the corresponding state string
         * @param multi the multi-variant
         */
        fun multiVariant(state: String, multi: Variant.Multi)

        /**
         * Adds a new multi-variant, which chooses a model randomly from its pool.
         * @param state the corresponding state string
         * @param init a block that initializes and modifies the multi-variant
         */
        fun multiVariant(state: String, init: InitFor<Variant.Multi.Scope>) {
            multiVariant(state, Variant.Multi(init))
        }

        /**
         * Shortcut of `variant` with an empty state string.
         * This is useful for blocks without properties.
         * @param model the model of the model
         */
        fun stateless(model: Variant.Simple) { variant("", model) }
        /**
         * Shortcut of `variant` with an empty state string.
         * This is useful for blocks without properties.
         * @param model the model ID of the model
         * @param init a block used to further modify the model (optional)
         */
        fun stateless(model: Identifier, init: InitFor<Variant.Simple> = {}) { variant("", model, init) }
        /**
         * Shortcut of `multiVariant` with an empty state string.
         * This is useful for blocks without properties.
         * @param multi the multi-variant to use
         */
        fun stateless(multi: Variant.Multi) { multiVariant("", multi) }
        /**
         * Shortcut of `multiVariant` with an empty state string.
         * This is useful for blocks without properties.
         * @param init a block that initializes and modifies the multi-variant
         */
        fun stateless(init: InitFor<Variant.Multi.Scope>) { multiVariant("", init) }
    }

    @DatakenesisDslMarker
    private inner class ScopeImpl: Scope {
        override fun variant(state: String, model: Variant.Simple) {
            _variants[state] = model
        }

        override fun multiVariant(state: String, multi: Variant.Multi) {
            _variants[state] = multi
        }
    }
}
