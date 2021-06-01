package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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

internal object UvSerializer: KSerializer<ModelElement.Uv> {
    private val surrogateSerializer = ListSerializer(Float.serializer())

    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun deserialize(decoder: Decoder): ModelElement.Uv {
        val list = decoder.decodeSerializableValue(surrogateSerializer)
        if (list.size != 4)
            throw IllegalStateException("expected exactly 3 floats in a UV coordinate, found ${list.size}: $list")
        return ModelElement.Uv(list[0], list[1], list[2], list[3])
    }

    override fun serialize(encoder: Encoder, value: ModelElement.Uv) {
        encoder.encodeSerializableValue(surrogateSerializer, listOf(value.x1, value.y1, value.x2, value.y2))
    }
}

internal object ModelTextureSerializer : JsonContentPolymorphicSerializer<ModelTexture>(ModelTexture::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ModelTexture> {
        if (!element.jsonPrimitive.isString)
            throw IllegalStateException("expected a string for texture variable, found $element")
        val str = element.jsonPrimitive.content
        return if (str.startsWith("#"))
            ModelTexture.Ref.serializer()
        else {
            ModelTexture.Var.serializer()
        }
    }
}

internal object ModelTextureRefSerializer: KSerializer<ModelTexture.Ref> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Ref", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ModelTexture.Ref {
        val str = decoder.decodeString()
        if (!str.startsWith("#"))
            throw IllegalStateException("texture reference does not start with #: $str")
        return ModelTexture.Ref(str.removePrefix("#"))
    }

    override fun serialize(encoder: Encoder, value: ModelTexture.Ref) {
        encoder.encodeString("#${value.id}")
    }
}

internal object MultipartWhenSerializer : JsonContentPolymorphicSerializer<MultipartWhen>(MultipartWhen::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element.jsonObject["OR"] is JsonArray -> MultipartWhen.Or.serializer()
        else -> MultipartWhen.State.serializer()
    }
}

internal object MultipartWhenStateSerializer: KSerializer<MultipartWhen.State> {
    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun deserialize(decoder: Decoder): MultipartWhen.State {
        val map = mapSerializer.deserialize(decoder)
        return MultipartWhen.State(map.toMutableMap())
    }

    override fun serialize(encoder: Encoder, value: MultipartWhen.State) {
        mapSerializer.serialize(encoder, value.states)
    }
}

internal object VariantSerializer : JsonContentPolymorphicSerializer<Variant>(Variant::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element.jsonObject["models"] is JsonArray -> Variant.Multi.serializer()
        else -> Variant.Simple.serializer()
    }
}

internal object VariantMultiAsSetSerializer: KSerializer<Variant.Multi> {
    private val setSerializer = SetSerializer(Variant.WeightedModel.serializer())

    override val descriptor: SerialDescriptor = setSerializer.descriptor

    override fun deserialize(decoder: Decoder): Variant.Multi {
        val set = setSerializer.deserialize(decoder)
        return Variant.Multi(set.toMutableSet())
    }

    override fun serialize(encoder: Encoder, value: Variant.Multi) {
        setSerializer.serialize(encoder, value.models)
    }
}

internal object Vec3fSerializer: KSerializer<Vec3f> {
    private val surrogateSerializer = ListSerializer(Float.serializer())

    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun deserialize(decoder: Decoder): Vec3f {
        val list = decoder.decodeSerializableValue(surrogateSerializer)
        if (list.size != 3)
            throw IllegalStateException("expected exactly 3 floats in a Vec3f, found ${list.size}: $list")
        return Vec3f(list[0], list[1], list[2])
    }

    override fun serialize(encoder: Encoder, value: Vec3f) {
        encoder.encodeSerializableValue(surrogateSerializer, listOf(value.x, value.y, value.z))
    }
}

internal object IdentifierSerializer: KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Identifier {
        return decoder.decodeString().asId
    }

    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }
}

internal object SlotsSerializer : JsonTransformingSerializer<List<Slot>>(
    ListSerializer(Slot.serializer())
) {
    // If response is not an array, then it is a single object that should be wrapped into the array
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}

internal object ItemModifierSerializer : KSerializer<ItemModifierInner> by DiscriminatorChanger(
    ItemModifierInner.serializer(), "function"
)

internal object NPIntSerializer : JsonContentPolymorphicSerializer<NumberProviderInt>(NumberProviderInt::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> NumberProviderInt.Constant.serializer()
        is JsonArray -> NumberProviderInt.Range.serializer()
        else -> throw IllegalStateException("unrecognized number provider type")
    }
}

internal object NPFloatSerializer : JsonContentPolymorphicSerializer<NumberProviderFloat>(NumberProviderFloat::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> NumberProviderFloat.Constant.serializer()
        is JsonArray -> NumberProviderFloat.Range.serializer()
        else -> throw IllegalStateException("unrecognized number provider type")
    }
}