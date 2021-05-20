package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@DslMarker
annotation class DatakenesisDslMarker

typealias InitFor<T> = T.() -> Unit

interface Builder<T> {
    fun build(): T
}

@Serializable(with = Identifier.Serializer::class)
data class Identifier(val namespace: String = "minecraft", val path: String) {
    init {
        if (!namespace.all { c -> c in '0'..'z' || c == '_' || c == '-' || c == '.' })
            throw IllegalArgumentException("namespace ($namespace) contains characters outside valid characters [0-9a-z_-.]")
        if (!path.all { c -> c in '0'..'z' || c == '_' || c == '-' || c == '.' || c == '/' })
            throw IllegalArgumentException("path ($path) contains characters outside valid characters [0-9a-z_-./]")
    }

    override fun toString(): String {
        return "$namespace:$path"
    }

    internal object Serializer: KSerializer<Identifier> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Identifier {
            return decoder.decodeString().asId
        }

        override fun serialize(encoder: Encoder, value: Identifier) {
            encoder.encodeString(value.toString())
        }
    }
}

val String.asId: Identifier
   get() {
       val split = this.split(":")
       return when (split.size) {
           1 -> Identifier(path = split[0])
           2 -> Identifier(split[0], split[1])
           else -> throw IllegalArgumentException("malformed identifier: $this is not a simple string nor a string in `namespace:path` form")
       }
   }

@Serializable(with = Vec3f.Serializer::class)
data class Vec3f(val x: Float, val y: Float, val z: Float) {
    companion object {
        val ORIGIN = Vec3f(0f, 0f, 0f)
    }

    internal object Serializer: KSerializer<Vec3f> {
        private val surrogateSerializer = ListSerializer(Float.serializer())

        override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

        override fun deserialize(decoder: Decoder): Vec3f {
            val list = decoder.decodeSerializableValue(surrogateSerializer)
            if (list.size != 3)
                throw IllegalStateException("expected exactly 3 floats in a Vec3f, found ${list.size}: $list")
            return Vec3f(list[0], list[1], list[2])
        }

        override fun serialize(encoder: Encoder, value: Vec3f) {
            encoder.encodeSerializableValue(surrogateSerializer, listOf(value.x, value.y, value.z))
        }
    }
}

@Serializable
enum class Axis {
    @SerialName("x") X,
    @SerialName("y") Y,
    @SerialName("z") Z
}

@Serializable
enum class Direction {
    @SerialName("down") DOWN,
    @SerialName("up") UP,
    @SerialName("north") NORTH,
    @SerialName("south") SOUTH,
    @SerialName("east") EAST,
    @SerialName("west") WEST,
}

@Serializable
enum class Position {
    @SerialName("thirdperson_righthand") THIRDPERSON_RIGHTHAND,
    @SerialName("thirdperson_lefthand") THIRDPERSON_LEFTHAND,
    @SerialName("firstperson_righthand") FIRSTPERSON_RIGHTHAND,
    @SerialName("firstperson_lefthand") FIRSTPERSON_LEFTHAND,
    @SerialName("gui") GUI,
    @SerialName("head") HEAD,
    @SerialName("ground") GROUND,
    @SerialName("fixed") FIXED,
}

@Serializable
enum class GuiLight {
    @SerialName("front") FRONT,
    @SerialName("side") SIDE,
}