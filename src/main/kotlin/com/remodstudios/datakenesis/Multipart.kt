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
data class Multipart(
    @SerialName("multipart")
    private val _cases: MutableSet<MultipartCase> = mutableSetOf()
) {
    val cases: Set<MultipartCase> by this::_cases

    fun case(case: MultipartCase) {
        _cases.add(case)
    }
    fun case(init: InitFor<MultipartCase>) {
        case(MultipartCase().apply(init))
    }

}

fun multipart(init: InitFor<Multipart>) = Multipart().apply(init)

@Serializable
data class MultipartCase(
    @SerialName("when")
    private var _when: MultipartWhen? = null,
    @SerialName("apply")
    private var _apply: Variant? = null,
) {
    fun whenState(init: InitFor<MultipartWhen.State>) {
        _when = MultipartWhen.State().apply(init)
    }
    fun whenOr(init: InitFor<MultipartWhen.Or>) {
        _when = MultipartWhen.Or().apply(init)
    }
    fun apply(model: Identifier, init: InitFor<Model> = {}) {
        _apply = Model(model).apply(init)
    }
    fun applyMultiple(init: InitFor<MultiVariant>) {
        _apply = MultiVariant().apply(init)
    }
}

@Serializable(with = MultipartWhen.Serializer::class)
sealed class MultipartWhen {
    @Serializable
    data class Or(
        @SerialName("OR")
        private val _states: MutableSet<State> = mutableSetOf()
    ): MultipartWhen() {
        val states: Set<State> by this::_states

        fun state(init: InitFor<State>) {
            _states.add(State().apply(init))
        }
    }

    @Serializable(with = State.Serializer::class)
    data class State(
        private val _props: MutableMap<String, String> = mutableMapOf()
    ): MultipartWhen() {
        val props: Map<String, String> by this::_props

        infix fun String.isValue(other: String) {
            _props[this] = other
        }
        infix fun String.isAnyIn(others: Collection<String>) {
            _props[this] = others.joinToString("|")
        }

        object Serializer: KSerializer<State> {
            private val setSerializer = MapSerializer(String.serializer(), String.serializer())

            override val descriptor: SerialDescriptor = setSerializer.descriptor

            override fun deserialize(decoder: Decoder): State {
                val map = setSerializer.deserialize(decoder)
                return State(map.toMutableMap())
            }

            override fun serialize(encoder: Encoder, value: State) {
                setSerializer.serialize(encoder, value._props)
            }
        }
    }

    object Serializer : JsonContentPolymorphicSerializer<MultipartWhen>(MultipartWhen::class) {
        override fun selectDeserializer(element: JsonElement) = when {
            element.jsonObject["OR"] is JsonArray -> Or.serializer()
            else -> State.serializer()
        }
    }
}




