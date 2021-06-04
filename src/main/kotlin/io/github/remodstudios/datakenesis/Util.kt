package io.github.remodstudios.datakenesis

import io.github.remodstudios.datakenesis.struct.IdentifierSerializer
import io.github.remodstudios.datakenesis.struct.Vec3fSerializer
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
import java.nio.file.Path

@DslMarker
annotation class DatakenesisDslMarker

typealias InitFor<T> = T.() -> Unit

interface Builder<T> {
    fun build(): T
}

@Serializable(with = IdentifierSerializer::class)
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

    fun newAffixed(prefix: String = "", suffix: String = "")
        = Identifier(namespace, "$prefix$path$suffix")
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

@Serializable(with = Vec3fSerializer::class)
data class Vec3f(val x: Float, val y: Float, val z: Float) {
    companion object {
        val ORIGIN = Vec3f(0f, 0f, 0f)
    }
}

