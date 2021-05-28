package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
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
                                 val levels: NumberProviderInt): ItemModifierInner()

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

@Serializable
data class AttributeModifier(val name: String,
                             val attribute: String,
                             val operation: Operation,
                             val amount: NumberProviderFloat,
                             val id: String = UUID.randomUUID().toString(),
                             val slot: Slots) {

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

typealias Slots = @Serializable(with = SlotsSerializer::class) List<Slot>

private object SlotsSerializer : JsonTransformingSerializer<List<Slot>>(
    ListSerializer(Slot.serializer())
) {
    // If response is not an array, then it is a single object that should be wrapped into the array
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}


class DiscriminatorChanger<T : Any>(
    private val tSerializer: KSerializer<T>,
    private val discriminator: String
) : KSerializer<T> {
    override val descriptor: SerialDescriptor get() = tSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        require(encoder is JsonEncoder)
        val json = Json(encoder.json) { classDiscriminator = discriminator }
        val element = json.encodeToJsonElement(tSerializer, value)
        encoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): T {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        val json = Json(decoder.json) { classDiscriminator = discriminator }
        return json.decodeFromJsonElement(tSerializer, element)
    }
}

internal object ItemModifierSerializer : KSerializer<ItemModifierInner> by DiscriminatorChanger(
    ItemModifierInner.serializer(), "function"
)

