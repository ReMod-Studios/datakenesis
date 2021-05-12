package com.remodstudios.datakenesis.test

import com.remodstudios.datakenesis.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class MultipartTest: FunSpec({
    context("DSL usability") {
        test("if the DSL functions exactly like manually written code") {
            example shouldBe manual
        }
    }
    context("JSON output") {
        test("if it outputs expected JSON") {
            val output = Json.encodeToString(example)

            output shouldBe """
                {"multipart":[{"apply":{"model":"minecraft:my_fence_post"}},{"when":{"north":"true"},"apply":{"model":"minecraft:my_fence_side"}},{"when":{"east":"true"},"apply":{"model":"minecraft:my_fence_side","y":90}},{"when":{"moisture":"7|15"},"apply":{"model":"just:a_totally_normal/farmland"}},{"when":{"OR":[{"north":"none","east":"none","south":"none","west":"none"},{"north":"side|up","east":"side|up"},{"east":"side|up","south":"side|up"},{"south":"side|up","west":"side|up"},{"west":"side|up","north":"side|up"}]},"apply":{"model":"minecraft:block/redstone_dust_dot"}}]}
            """.trimIndent()
        }
    }
})

private val example = multipart {
    case {
        apply("my_fence_post".asId())
    }
    case {
        whenState { "north" isValue "true" }
        apply("my_fence_side".asId())
    }
    case {
        whenState { "east" isValue "true" }
        apply("my_fence_side".asId()) {
            y = 90
        }
    }
    case {
        whenState { "moisture" isValue "7|15" }
        apply("just:a_totally_normal/farmland".asId())
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
        apply("block/redstone_dust_dot".asId())
    }
}

// construct an equivalent object to `example` manually
private val manual by lazy {
    val manual = Multipart()
    manual.case(MultipartCase(
        _apply = Model(
            "my_fence_post".asId()
        )
    ))
    manual.case(MultipartCase(
        MultipartWhen.State(mutableMapOf(
            "north" to "true"
        )),
        Model(
            "my_fence_side".asId()
        )
    ))
    manual.case(MultipartCase(
        MultipartWhen.State(mutableMapOf(
            "east" to "true"
        )),
        Model(
            "my_fence_side".asId(),
            y = 90
        )
    ))
    manual.case(MultipartCase(
        MultipartWhen.State(mutableMapOf(
            "moisture" to "7|15"
        )),
        Model(
            "just:a_totally_normal/farmland".asId()
        )
    ))
    manual.case(MultipartCase(
        MultipartWhen.Or(mutableSetOf(
            MultipartWhen.State(mutableMapOf(
                "north" to "none",
                "east" to "none",
                "south" to "none",
                "west" to "none"
            )),
            MultipartWhen.State(mutableMapOf(
                "north" to "side|up",
                "east" to "side|up",
            )),
            MultipartWhen.State(mutableMapOf(
                "east" to "side|up",
                "south" to "side|up",
            )),
            MultipartWhen.State(mutableMapOf(
                "south" to "side|up",
                "west" to "side|up",
            )),
            MultipartWhen.State(mutableMapOf(
                "west" to "side|up",
                "north" to "side|up",
            )),
        )),
        Model(
            "block/redstone_dust_dot".asId()
        )
    ))
    manual
}
