package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Model(
    val parent: Identifier? = null,
    @SerialName("ambientocclusion")
    val ambientOcclusion: Boolean = true,
    val display: Map<Position, ModelDisplay>,
    val textures: Map<String, ModelTexture>,
    val elements: List<ModelElement>,
)

inline fun model(init: InitFor<ModelBuilder>) = ModelBuilder().apply(init).build()

@DatakenesisDslMarker
class ModelBuilder: Builder<Model> {
    var parent: Identifier? = null
    var ambientOcclusion: Boolean = true
    val display: MutableMap<Position, ModelDisplay> = mutableMapOf()
    val textures: MutableMap<String, ModelTexture> = mutableMapOf()
    val elements: MutableList<ModelElement> = mutableListOf()

    override fun build() = Model(parent, ambientOcclusion, display, textures, elements)

    fun displays(init: InitFor<DisplaysScope>) { DisplaysScope().init() }
    fun textures(init: InitFor<TexturesScope>) { TexturesScope().init() }
    fun elements(init: InitFor<ElementsScope>) { ElementsScope().init() }


    @DatakenesisDslMarker
    inner class DisplaysScope {
        operator fun Position.invoke(rotation: Vec3f = Vec3f(0f, 0f, 0f),
                                     translation: Vec3f = Vec3f(0f, 0f, 0f),
                                     scale: Vec3f = Vec3f(1f, 1f, 1f)
        ) {
            this@ModelBuilder.display[this] = ModelDisplay(rotation, translation, scale)
        }
    }

    @DatakenesisDslMarker
    inner class TexturesScope {

        operator fun String.invoke(texture: ModelTexture) { this@ModelBuilder.textures[this] = texture }
        operator fun String.invoke(tex: Identifier) { this@ModelBuilder.textures[this] = ModelTexture.Var(tex)
        }
        operator fun String.invoke(ref: String) { this@ModelBuilder.textures[this] = ModelTexture.Ref(ref)
        }
    }

    @DatakenesisDslMarker
    inner class ElementsScope {

        val elements: MutableList<ModelElement> = this@ModelBuilder.elements

        fun add(element: ModelElement) { elements.add(element) }
        inline fun add(from: Vec3f, to: Vec3f, init: InitFor<ModelElementBuilder>) {
            elements.add(ModelElementBuilder(from, to).apply(init).build())
        }
        inline fun add(x1: Float, y1: Float, z1: Float,
                x2: Float, y2: Float, z2: Float,
                init: InitFor<ModelElementBuilder>
        ) {
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

