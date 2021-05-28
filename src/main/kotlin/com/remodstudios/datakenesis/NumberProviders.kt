package com.remodstudios.datakenesis

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable(with = NPIntSerializer::class)
sealed interface NumberProviderInt {
    @JvmInline
    @Serializable
    value class Constant(val value: Int): NumberProviderInt

    @Serializable
    data class Range(val min: Int,
                     val max: Int): NumberProviderInt
}

private object NPIntSerializer : JsonContentPolymorphicSerializer<NumberProviderInt>(NumberProviderInt::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> NumberProviderInt.Constant.serializer()
        is JsonArray -> NumberProviderInt.Range.serializer()
        else -> throw IllegalStateException("unrecognized number provider type")
    }
}

@Serializable(with = NPFloatSerializer::class)
sealed interface NumberProviderFloat {
    @JvmInline
    @Serializable
    value class Constant(val value: Float): NumberProviderFloat

    @Serializable
    data class Range(val min: Float,
                     val max: Float): NumberProviderFloat
}

private object NPFloatSerializer : JsonContentPolymorphicSerializer<NumberProviderFloat>(NumberProviderFloat::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> NumberProviderFloat.Constant.serializer()
        is JsonArray -> NumberProviderFloat.Range.serializer()
        else -> throw IllegalStateException("unrecognized number provider type")
    }
}