package com.remodstudios.datakenesis

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
@DatakenesisDslMarker
data class Multipart(
    @SerialName("multipart")
    val cases: Set<MultipartCase>
)

fun multipart(init: InitFor<MultipartBuilder>) = MultipartBuilder().apply(init).build()


@DatakenesisDslMarker
class MultipartBuilder {
    val cases: MutableSet<MultipartCase> = mutableSetOf()

    fun build() = Multipart(cases.toSet())

    fun case(case: MultipartCase) { cases.add(case) }

    inline fun case(init: InitFor<MultipartCaseBuilder>) {
        cases.add(MultipartCaseBuilder().apply(init).build())
    }
}

@Serializable
data class MultipartCase(
    @SerialName("when")
    val whenCriteria: MultipartWhen?,
    val apply: Variant,
)

@DatakenesisDslMarker
class MultipartCaseBuilder {
    var whenCriteria: MultipartWhen? = null
    var apply: Variant? = null

    fun build() = MultipartCase(
        whenCriteria,
        apply ?: throw IllegalStateException("`apply` not specified")
    )

    inline fun whenState(init: InitFor<MultipartWhen.StateBuilder>) {
        whenCriteria = MultipartWhen.StateBuilder().apply(init).build()
    }
    inline fun whenOr(init: InitFor<MultipartWhen.OrBuilder>) {
        whenCriteria = MultipartWhen.OrBuilder().apply(init).build()
    }
    fun apply(model: Identifier,
              x: Int = 0,
              y: Int = 0,
              uvlock: Boolean = false) {
        apply = Variant.Simple(model, x, y, uvlock)
    }
    inline fun applyMulti(init: InitFor<Variant.MultiBuilder>) {
        apply = Variant.MultiBuilder().apply(init).build()
    }
}

@Serializable(with = MultipartWhenSerializer::class)
sealed interface MultipartWhen {
    @Serializable
    data class Or(
        @SerialName("OR")
        val states: Set<State>
    ): MultipartWhen

    @DatakenesisDslMarker
    class OrBuilder(
        val states: MutableSet<State> = mutableSetOf()
    ): MultipartWhen, MutableSet<State> by states {
        fun build() = Or(states.toSet())

        inline fun add(init: InitFor<StateBuilder>) {
            states.add(StateBuilder().apply(init).build())
        }
    }

    @Serializable(with = State.Serializer::class)
    data class State(val states: Map<String, String>): MultipartWhen {
        internal object Serializer: KSerializer<State> {
            private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

            override val descriptor: SerialDescriptor = mapSerializer.descriptor

            override fun deserialize(decoder: Decoder): State {
                val map = mapSerializer.deserialize(decoder)
                return State(map.toMutableMap())
            }

            override fun serialize(encoder: Encoder, value: State) {
                mapSerializer.serialize(encoder, value.states)
            }
        }
    }

    @DatakenesisDslMarker
    class StateBuilder {
        val props: MutableMap<String, String> = mutableMapOf()

        fun build() = State(props.toMap())

        fun expect(prop: String, vararg states: String) {
            props[prop] = states.joinToString("|")
        }
    }
}

private object MultipartWhenSerializer : JsonContentPolymorphicSerializer<MultipartWhen>(MultipartWhen::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element.jsonObject["OR"] is JsonArray -> MultipartWhen.Or.serializer()
        else -> MultipartWhen.State.serializer()
    }
}


