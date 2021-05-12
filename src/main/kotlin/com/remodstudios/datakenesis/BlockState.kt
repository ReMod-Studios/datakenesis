package com.remodstudios.datakenesis

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias InitFor<T> = T.() -> Unit

@Serializable
data class BlockState(
    @SerialName("variants")
    private val _variants: MutableMap<String, @Contextual Variant> = mutableMapOf()
) {
    val variants: Map<String, Variant> by this::_variants

    fun variant(id: String, model: Model) { _variants[id] = model }

    fun variant(id: String, model: Identifier, init: InitFor<Model>) {
        variant(id, Model(model).apply(init))
    }

    fun multiVariant(id: String, multi: MultiVariant) {
        _variants[id] = multi
    }

    fun multiVariant(id: String, init: InitFor<MultiVariant>) {
        multiVariant(id, MultiVariant().apply(init))
    }

    fun stateless(model: Model) { variant("", model) }
    fun stateless(model: Identifier, init: InitFor<Model>) { variant("", model, init) }
}

fun blockState(init: InitFor<BlockState>) = BlockState().apply(init)