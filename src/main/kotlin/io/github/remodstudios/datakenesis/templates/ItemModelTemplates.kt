package io.github.remodstudios.datakenesis.templates

import io.github.remodstudios.datakenesis.Identifier

data class MaybeParent(
    override var id: Identifier,
    var parent: Identifier = BuiltinParents.GENERATED,
): TemplateData


object ItemModelTemplate: DslTemplate<MaybeParent>(
    ::MaybeParent,
    {
        val item = it.id.newAffixed(prefix = "item/")

        model(item) {
            parent = BuiltinParents.GENERATED
            textures {
                "layer0"(item)
            }
        }
    }
)

object BlockItemModelTemplate: DslTemplate<IdentifierData>(
    ::IdentifierData,
    {
        val item = it.id.newAffixed(prefix = "item/")

        model(item) {
            parent = it.id.newAffixed(prefix = "block/")
        }
    }
)