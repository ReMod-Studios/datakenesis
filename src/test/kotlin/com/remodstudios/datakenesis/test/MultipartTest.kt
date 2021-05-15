package com.remodstudios.datakenesis.test

import com.remodstudios.datakenesis.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class MultipartTest: FunSpec({
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = Json.encodeToString(example)

            println(output)
        }
    }
})

private val example = Multipart {
    case {
        apply("my_fence_post".asId)
    }
    case {
        whenState { "north" isValue "true" }
        apply("my_fence_side".asId)
    }
    case {
        whenState { "east" isValue "true" }
        apply("my_fence_side".asId) {
            y = 90
        }
    }
    case {
        whenState { "moisture" isValue "7|15" }
        apply("just:a_totally_normal/farmland".asId)
    }
    case {
        whenOr {
            // damn this is complicated
            state {
                "north" isValue "none"
                "east" isValue "none"
                "south" isValue "none"
                "west" isValue "none"
            }
            state {
                "north" isAnyIn listOf("side", "up")
                "east" isAnyIn listOf("side", "up")
            }
            state {
                "east" isAnyIn listOf("side", "up")
                "south" isAnyIn listOf("side", "up")
            }
            state {
                "south" isAnyIn listOf("side", "up")
                "west" isAnyIn listOf("side", "up")
            }
            state {
                "west" isAnyIn listOf("side", "up")
                "north" isAnyIn listOf("side", "up")
            }
        }
        apply("block/redstone_dust_dot".asId)
    }
}