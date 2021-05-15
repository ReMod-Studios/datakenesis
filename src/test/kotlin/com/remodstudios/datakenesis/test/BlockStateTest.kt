package com.remodstudios.datakenesis.test

import com.remodstudios.datakenesis.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement


class BlockStateTest: FunSpec({
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = json.encodeToString(example)

            println(output)
            output shouldBe """
                {
                    "variants": {
                        "": {
                            "model": "minecraft:haha",
                            "x": 90,
                            "uvlock": true
                        },
                        "facing=north": {
                            "model": "minecraft:haha_north",
                            "x": 180
                        },
                        "facing=south": [
                            {
                                "model": "minecraft:haha_normal",
                                "x": 90,
                                "y": 270,
                                "weight": 100
                            },
                            {
                                "model": "minecraft:haha_rare",
                                "x": 90,
                                "y": 90,
                                "weight": 2
                            }
                        ]
                    }
                }
            """.trimIndent()
        }
    }
})

private val json = Json {
    prettyPrint = true
}

private val example = BlockState {
    stateless("haha".asId) {
        x = 90
        uvlock = true
    }
    variant("facing=north", "haha_north".asId) {
        x = 180
    }
    multiVariant("facing=south") {
        model("haha_normal".asId) {
            x = 90
            y = 270
            weight = 100
        }
        model("haha_rare".asId) {
            x = 90
            y = 90
            weight = 2
        }
    }
}