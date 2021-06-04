package io.github.remodstudios.datakenesis.templates

import io.github.remodstudios.datakenesis.Identifier

interface TemplateData {
    var id: Identifier
}

data class IdentifierData(
    override var id: Identifier
): TemplateData