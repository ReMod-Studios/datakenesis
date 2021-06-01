package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class VillagerTrades(
    val tiers: List<Tier>
)

@Serializable
data class Tier(
    val trades: List<Trade>
)

@Serializable
data class Trade(
    val wants: List<Want>,
    val gives: List<Give>
)

@Serializable
data class Want(
    val item: String,
    val quantity: NumberProviderInt
)

@Serializable
data class Give(
    val item: String,
    val functions: List<ItemModifier> = listOf()
)