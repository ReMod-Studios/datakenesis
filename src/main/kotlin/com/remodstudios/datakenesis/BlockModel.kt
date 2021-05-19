package com.remodstudios.datakenesis

import kotlinx.serialization.Serializable

@Serializable
data class BlockModel(
    override val parent: Identifier? = null,
    override val ambientOcclusion: Boolean = true,
    override val display: Map<Position, ModelDisplay>,
    override val textures: Map<String, ModelTexture>,
    override val elements: List<ModelElement>,
): BaseModel()

inline fun blockModel(init: InitFor<BlockModelBuilder>) = BlockModelBuilder().apply(init).build()

class BlockModelBuilder: BaseModelBuilder<BlockModel>() {
    override fun build() = BlockModel(parent, ambientOcclusion, display, textures, elements)
}