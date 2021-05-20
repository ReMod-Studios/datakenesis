package com.remodstudios.datakenesis

import kotlinx.serialization.Serializable

@Serializable
data class BlockModel(
    override val parent: Identifier?,
    override val ambientOcclusion: Boolean,
    override val _display: MutableMap<Position, ModelDisplay>,
    override val _textures: MutableMap<String, ModelTexture>,
    override val _elements: MutableList<ModelElement>,
): BaseModel()

inline fun blockModel(init: InitFor<BlockModelBuilder>) = BlockModelBuilder().apply(init).build()