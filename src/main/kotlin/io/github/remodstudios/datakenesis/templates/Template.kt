package io.github.remodstudios.datakenesis.templates

import io.github.remodstudios.datakenesis.Generator
import io.github.remodstudios.datakenesis.Identifier

interface Template<Data: TemplateData> {
    fun generate(generator: Generator, data: Data)
    fun makeData(id: Identifier): Data
}

abstract class DslTemplate<Data: TemplateData>(
    val dataFactory: (Identifier) -> Data,
    val generate: Generator.(Data) -> Unit
): Template<Data> {
    override fun generate(generator: Generator, data: Data) {
        generator.generate(data)
    }

    override fun makeData(id: Identifier): Data = dataFactory(id)
}