package io.github.remodstudios.datakenesis.test

import io.github.remodstudios.datakenesis.struct.asId
import io.github.remodstudios.datakenesis.struct.multipart
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MultipartTest: FunSpec({
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = Json.encodeToString(example)

            println(output)
        }
    }
})

private val example = multipart {
    case {
        apply("my_fence_post".asId)
    }
    case {
        whenState {
            expect("north", "true")
        }
        apply("my_fence_side".asId)
    }
    case {
        whenState { expect("east", "true") }
        apply("my_fence_side".asId, y = 90)
    }
    case {
        whenState { expect("moisture", "7|15") }
        apply("just:a_totally_normal/farmland".asId)
    }
    case {
        whenOr {
            // damn this is complicated
            add {
                expect("north", "none")
                expect("east", "none")
                expect("south", "none")
                expect("west", "none")
            }
            add {
                expect("north", "side", "up")
                expect("east", "side", "up")
            }
            add {
                expect("east", "side", "up")
                expect("south", "side", "up")
            }
            add {
                expect("east", "side", "up")
                expect("west", "side", "up")
            }
            add {
                expect("west", "side", "up")
                expect("north", "side", "up")
            }
        }
        apply("block/redstone_dust_dot".asId)
    }
}