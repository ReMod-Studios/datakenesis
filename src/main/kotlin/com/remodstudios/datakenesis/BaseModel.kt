package com.remodstudios.datakenesis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class BaseModel {
    open val parent: Identifier? = null

    @SerialName("ambientocclusion")
    open val ambientOcclusion: Boolean = true

    // haha nice boilerplate
    @SerialName("display")
    protected open val _display: MutableMap<Position, ModelDisplay> = mutableMapOf()
    val display: Map<Position, ModelDisplay> get() = _display

    @SerialName("textures")
    protected open val _textures: MutableMap<String, ModelTexture> = mutableMapOf()
    val textures: Map<String, ModelTexture> get() = _textures

    @SerialName("elements")
    protected open val _elements: MutableList<ModelElement> = mutableListOf()
    val elements: List<ModelElement> get() = _elements

    fun displays(init: InitFor<DisplaysScope>) { DisplaysScope(_display).init() }
    fun textures(init: InitFor<TexturesScope>) { TexturesScope(_textures).init() }
    fun elements(init: InitFor<ElementsScope>) { ElementsScope(_elements).init() }

    @DatakenesisDslMarker
    @JvmInline
    value class DisplaysScope(val display: MutableMap<Position, ModelDisplay>) {
        operator fun Position.invoke(rotation: Vec3f = Vec3f(0f, 0f, 0f),
                                     translation: Vec3f = Vec3f(0f, 0f, 0f),
                                     scale: Vec3f = Vec3f(1f, 1f, 1f)) {
            display[this] = ModelDisplay(rotation, translation, scale)
            
        }
    }

    @DatakenesisDslMarker
    @JvmInline
    value class TexturesScope(val textures: MutableMap<String, ModelTexture>) {
        operator fun String.invoke(texture: ModelTexture) { textures[this] = texture }
        operator fun String.invoke(tex: Identifier) { textures[this] = ModelTexture.Var(tex) }
        operator fun String.invoke(ref: String) { textures[this] = ModelTexture.Ref(ref) }
    }

    @DatakenesisDslMarker
    @JvmInline
    value class ElementsScope(val elements: MutableList<ModelElement>) {
        fun add(element: ModelElement) { elements.add(element) }
        inline fun add(from: Vec3f, to: Vec3f, init: InitFor<ModelElementBuilder>) {
            elements.add(ModelElementBuilder(from, to).apply(init).build())
        }
        inline fun add(x1: Float, y1: Float, z1: Float,
                       x2: Float, y2: Float, z2: Float,
                       init: InitFor<ModelElementBuilder>) {
            elements.add(ModelElementBuilder(Vec3f(x1, y1, z1), Vec3f(x2, y2, z2)).apply(init).build())
        }
    }
}

@Serializable
data class ModelDisplay(
    val rotation: Vec3f = Vec3f(0f, 0f, 0f),
    val translation: Vec3f = Vec3f(0f, 0f, 0f),
    val scale: Vec3f = Vec3f(1f, 1f, 1f),
)

@Serializable
data class ModelElement(
    val from: Vec3f,
    val to: Vec3f,
    val rotation: Rotation? = null,
    val shade: Boolean = true,
    val faces: Map<Direction, Face> = mapOf(),
) {
    @Serializable
    data class Rotation(
        val origin: Vec3f,
        val axis: Axis,
        val angle: Float, // TODO: warn users for using values that are incompatible with vanilla
        val rescale: Boolean = false
    )

    @Serializable
    data class Face(
        val texture: ModelTexture.Ref,
        val uv: Uv? = null,
        @SerialName("cullface")
        val cullFace: Direction? = null,
        val rotation: Int = 0, // TODO: warn users for using values that are incompatible with vanilla
        @SerialName("tintindex")
        val tintIndex: Int = -1,
    )

    @Serializable(with = UvSerializer::class)
    data class Uv(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    )
}

@DatakenesisDslMarker
class ModelElementBuilder(val from: Vec3f, val to: Vec3f): Builder<ModelElement> {
    var rotation: ModelElement.Rotation? = null
    var shade: Boolean = true
    val faces: MutableMap<Direction, ModelElement.Face> = mutableMapOf()

    override fun build() = ModelElement(from, to, rotation, shade, faces)

    operator fun Direction.invoke(
        texture: ModelTexture.Ref,
        uv: ModelElement.Uv? = null,
        cullFace: Direction? = null,
        rotation: Int = 0,
        tintIndex: Int = -1,
    ) {
        faces[this] = ModelElement.Face(texture, uv, cullFace, rotation, tintIndex)
    }

    fun allFaces(
        texture: ModelTexture.Ref,
        uv: ModelElement.Uv? = null,
        cullFace: Direction? = null,
        rotation: Int = 0,
        tintIndex: Int = -1,
    ) {
        val face = ModelElement.Face(texture, uv, cullFace, rotation, tintIndex)
        for (d in Direction.values())
            faces[d] = face
    }
}

@Serializable(with = ModelTextureSerializer::class)
sealed interface ModelTexture {
    @Serializable
    @JvmInline
    value class Var(val id: Identifier): ModelTexture

    @Serializable(with = ModelTextureRefSerializer::class)
    @JvmInline
    value class Ref(val id: String): ModelTexture
}

