package com.remodstudios.datakenesis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemModel(
    override val parent: Identifier?,
    override val ambientOcclusion: Boolean?,
    override val display: Map<Position, ModelDisplay>?,
    override val textures: Map<String, ModelTexture>?,
    override val elements: List<ModelElement>?,
    @SerialName("gui_light")
    val guiLight: GuiLight?,
    val overrides: Set<ModelOverride>?,
): BaseModel()

inline fun itemModel(init: InitFor<ItemModelBuilder>) = ItemModelBuilder().apply(init).build()

@DatakenesisDslMarker
class ItemModelBuilder: BaseModelBuilder<ItemModel>() {
    var guiLight: GuiLight? = null
    var overrides: MutableSet<ModelOverride>? = null

    override fun build() = ItemModel(parent, ambientOcclusion, display, textures, elements, guiLight, overrides)

    inline fun overrides(init: InitFor<OverridesScope>) { overrides = OverridesScope(mutableSetOf()).apply(init).overrides }

    @DatakenesisDslMarker
    @JvmInline
    value class OverridesScope(val overrides: MutableSet<ModelOverride>) {
        fun add(override: ModelOverride) { overrides.add(override) }
        fun add(model: Identifier, vararg predicates: Pair<String, Float>) {
            overrides.add(ModelOverride(mapOf(*predicates), model))
        }
    }
}

@Serializable
data class ModelOverride(val predicate: Map<String, Float>,
                         val model: Identifier)
