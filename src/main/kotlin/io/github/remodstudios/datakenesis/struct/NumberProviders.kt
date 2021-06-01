package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.Serializable

@Serializable(with = NPIntSerializer::class)
sealed interface NumberProviderInt {
    @JvmInline
    @Serializable
    value class Constant(val value: Int): NumberProviderInt

    @Serializable
    data class Range(val min: Int,
                     val max: Int): NumberProviderInt
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
