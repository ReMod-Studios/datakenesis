package com.remodstudios.datakenesis

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
