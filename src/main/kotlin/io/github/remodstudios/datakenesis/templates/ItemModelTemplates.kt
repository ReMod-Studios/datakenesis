package io.github.remodstudios.datakenesis.templates

import io.github.remodstudios.datakenesis.struct.Identifier

data class IdAndMaybeParent(
    val id: Identifier,
    val parent: Identifier = BuiltinParents.GENERATED,
)


object ItemModelTemplate: Template<IdAndMaybeParent>({
    val item = it.id.newAffixed(prefix = "item/")

    model(item) {
        parent = BuiltinParents.GENERATED
        textures {
            "layer0"(item)
        }
    }
})

object BlockItemModelTemplate: Template<Identifier>({
    val item = it.newAffixed(prefix = "item/")

    model(item) {
        parent = it.newAffixed(prefix = "block/")
    }
})