package io.github.remodstudios.datakenesis.test

import io.github.remodstudios.datakenesis.struct.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ModelTest: FunSpec({
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = json.encodeToString(example)

            println(output)
            output shouldBe """
                {
                    "parent": "minecraft:block/cube_all",
                    "display": {
                        "head": {
                            "rotation": [
                                0.0,
                                35.0,
                                62.5
                            ],
                            "translation": [
                                0.0,
                                4.5,
                                1.0
                            ],
                            "scale": [
                                1.5,
                                1.25,
                                2.1
                            ]
                        }
                    },
                    "textures": {
                        "layer0": "hello:block/this_is_my_first_block_model",
                        "layer1": "hello:block/overlay",
                        "tex": "#layer0"
                    },
                    "elements": [
                        {
                            "from": [
                                1.0,
                                2.0,
                                7.0
                            ],
                            "to": [
                                7.0,
                                5.0,
                                3.0
                            ],
                            "rotation": {
                                "origin": [
                                    0.0,
                                    0.0,
                                    0.0
                                ],
                                "axis": "y",
                                "angle": 22.5
                            },
                            "faces": {
                                "down": {
                                    "texture": "#tex"
                                },
                                "north": {
                                    "texture": "#layer0",
                                    "uv": [
                                        3.1,
                                        5.8,
                                        1.0,
                                        15.5
                                    ],
                                    "cullface": "up",
                                    "rotation": 90,
                                    "tintindex": 8516321
                                }
                            }
                        },
                        {
                            "from": [
                                4.0,
                                4.0,
                                4.0
                            ],
                            "to": [
                                12.0,
                                12.0,
                                12.0
                            ],
                            "faces": {
                                "down": {
                                    "texture": "#tex"
                                },
                                "up": {
                                    "texture": "#tex"
                                },
                                "north": {
                                    "texture": "#tex"
                                },
                                "south": {
                                    "texture": "#tex"
                                },
                                "east": {
                                    "texture": "#tex"
                                },
                                "west": {
                                    "texture": "#tex"
                                }
                            }
                        }
                    ]
                }
            """.trimIndent()
        }
    }
})

private val json = Json {
    prettyPrint = true
}

private val example = model {
    // this will end up getting ignored. eh, whatever
    parent = "minecraft:block/cube_all".asId

    displays {
        Position.HEAD (
            rotation = Vec3f(0f, 35f, 62.5f),
            translation = Vec3f(0f, 4.5f, 1f),
            scale = Vec3f(1.5f, 1.25f, 2.1f)
        )
    }

    textures {
        "layer0"(tex = "hello:block/this_is_my_first_block_model".asId)
        "layer1"(tex = "hello:block/overlay".asId)
        "tex"(ref = "layer0")
    }

    elements {
        add(
            1f, 2f, 7f,
            7f, 5f, 3f,
        ) {
            rotation = ModelElement.Rotation(Vec3f.ORIGIN, Axis.Y, 22.5f)
            shade = true
            Direction.DOWN(
                texture = ModelTexture.Ref("tex"),
            )
            Direction.NORTH(
                texture = ModelTexture.Ref("layer0"),
                uv = ModelElement.Uv(3.1f, 5.8f, 1f, 15.5f),
                cullFace = Direction.UP,
                rotation = 90,
                tintIndex = 0x81F2E1
            )
        }
        add(
            4f, 4f, 4f,
            12f, 12f, 12f,
        ) {
            allFaces(ModelTexture.Ref("tex"))
        }
    }
}