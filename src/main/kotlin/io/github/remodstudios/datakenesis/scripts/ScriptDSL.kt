package io.github.remodstudios.datakenesis.scripts

import io.github.remodstudios.datakenesis.Generator
import io.github.remodstudios.datakenesis.Identifier
import io.github.remodstudios.datakenesis.templates.Template
import io.github.remodstudios.datakenesis.templates.TemplateData

inline fun ids(namespace: String, scope: NamespaceAbbreviatedExecScope.() -> Unit)
    = NamespaceAbbreviatedExecScope(namespace).apply(scope)

class NamespaceAbbreviatedExecScope(
    val namespace: String,
    val generator: Generator = Generator()
) {
    fun id(path: String) = Identifier(namespace, path)

    inline fun <reified Data: TemplateData> entry(
        entry: String,
        template: Template<Data>,
        dataInit: Data.() -> Unit,
    ) {
        val id = id(entry)
        val data = template.makeData(id)
        data.dataInit()
        template.generate(generator, data)
    }

}