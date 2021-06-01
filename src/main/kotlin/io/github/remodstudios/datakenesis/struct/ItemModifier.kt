package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

// had to do this jank
// see https://github.com/Kotlin/kotlinx.serialization/issues/546 for more info
typealias ItemModifier = @Serializable(with = ItemModifierSerializer::class) ItemModifierInner

@Serializable
sealed class ItemModifierInner {
    @Serializable
    @SerialName("copy_name")
    data class CopyName(
        val source: Source
    ): ItemModifierInner() {
        init { require(source == Source.BLOCK_ENTITY) }
    }

    @Serializable
    @SerialName("copy_state")
    data class CopyState(val block: Identifier,
                         val properties: List<String>): ItemModifierInner()

    @Serializable
    @SerialName("enchant_randomly")
    data class EnchantRandomly(val enchantments: List<Identifier>): ItemModifierInner()

    @Serializable
    @SerialName("enchant_with_levels")
    data class EnchantWithLevels(val treasure: Boolean,
                                 val levels: NumberProviderInt
    ): ItemModifierInner()

    @Serializable
    @SerialName("explosion_decay")
    object ExplosionDecay: ItemModifierInner()

    @Serializable
    @SerialName("furnace_smelt")
    object FurnaceSmelt: ItemModifierInner()

    @Serializable
    @SerialName("fill_player_head")
    data class FillPlayerHead(val entity: Source): ItemModifierInner()

    @Serializable
    @SerialName("limit_count")
    data class LimitCount(val limit: NumberProviderInt): ItemModifierInner()

    @Serializable
    @SerialName("looting_enchant")
    data class LootingEnchant(val count: NumberProviderInt,
                              val limit: Int): ItemModifierInner()

    @Serializable
    @SerialName("set_attributes")
    data class SetAttributes(val modifiers: List<AttributeModifier>): ItemModifierInner()



    @Serializable
    enum class Source {
        @SerialName("block_entity") BLOCK_ENTITY,
        @SerialName("this") THIS,
        @SerialName("killer") KILLER,
        @SerialName("killer_player") KILLER_PLAYER
    }
}

typealias Slots = @Serializable(with = SlotsSerializer::class) List<Slot>

@Serializable
data class AttributeModifier(val name: String,
                             val attribute: String,
                             val operation: Operation,
                             val amount: NumberProviderFloat,
                             val id: String = UUID.randomUUID().toString(),
                             val slot: Slots
) {

    @Serializable
    enum class Operation {
        @SerialName("addition") ADDITION,
        @SerialName("multiply_base") MULTIPLY_BASE,
        @SerialName("multiply_total") MULTIPLY_TOTAL
    }
}

@Serializable
enum class Slot {
    @SerialName("mainhand") MAINHAND,
    @SerialName("offhand") OFFHAND,
    @SerialName("head") HEAD,
    @SerialName("chest") CHEST,
    @SerialName("legs") LEGS,
    @SerialName("feet") FEET
}

