package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@DslMarker
annotation class DatakenesisDslMarker

typealias InitFor<T> = T.() -> Unit

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
