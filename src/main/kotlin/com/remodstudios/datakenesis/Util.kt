package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = IdentifierSerializer::class)
data class Identifier(val namespace: String = "minecraft", val path: String) {
    init {
        if (!namespace.all { c -> c in '0'..'z' || c == '_' || c == '-' || c == '.' })
            throw IllegalArgumentException("namespace contains characters outside valid characters [0-9a-z_-.]")
        if (!path.all { c -> c in '0'..'z' || c == '_' || c == '-' || c == '.' || c == '/' })
            throw IllegalArgumentException("path contains characters outside valid characters [0-9a-z_-./]")
    }

    override fun toString(): String {
        return "$namespace:$path"
    }
}

fun String.asId(): Identifier {
    val (namespace, path) = this.split(":")
    return Identifier(namespace, path)
}

object IdentifierSerializer: KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Identifier {
        return decoder.decodeString().asId()
    }

    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }

}