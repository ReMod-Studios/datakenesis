package com.remodstudios.datakenesis.test

import com.remodstudios.datakenesis.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ItemModelTest: FunSpec({
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = json.encodeToString(example)

            println(output)
        }
    }
})

private val json = Json {
    prettyPrint = true
}

private val example = itemModel {
    // this will end up getting ignored. eh, whatever
    parent = "minecraft:item/handheld".asId

    displays {
        Position.HEAD (
            rotation = Vec3f(0f, 35f, 62.5f),
            translation = Vec3f(0f, 4.5f, 1f),
            scale = Vec3f(1.5f, 1.25f, 2.1f)
        )
    }

    textures {
        "layer0"(tex = "hello:item/my_epic_sword".asId)
        "layer1"(tex = "hello:item/my_epic_sword_sheen".asId)
    }

    guiLight = GuiLight.FRONT

    overrides {
        add("hello:item/rzhanoj_khleb".asId,
            "custom_model_data" to 9870002f)
        add("hello:item/my_epic_sword_night".asId,
            "time" to 0.5f,
            "custom_model_data" to 114514f)
    }
}