package io.github.remodstudios.datakenesis.templates

import io.github.remodstudios.datakenesis.Generator

open class Template<Data>(
    val generate: Generator.(Data) -> Unit
)