package io.github.remodstudios.datakenesis

import io.github.remodstudios.datakenesis.struct.ModelBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

class Generator(val files: MutableMap<Path, ByteArray> = mutableMapOf(),
                val json: Json = Json.Default) {

    inline fun json(path: Path, obj: () -> Any) {
        files[path] = json.encodeToString(json.serializersModule.serializer(), obj()).encodeToByteArray()
    }

    inline fun model(id: Identifier, init: InitFor<ModelBuilder>) {
        val path = Path.of("assets/${id.namespace}/models/${id.path}")
        json(path) { ModelBuilder().init() }
    }

    fun export(dir: Path) {
        if (!dir.isDirectory()) throw IllegalArgumentException("path is not a directory")

        // TODO: is this inefficient?
        for ((path, data) in files) {
            Files.write(dir.resolve(path), data)
        }
    }
}