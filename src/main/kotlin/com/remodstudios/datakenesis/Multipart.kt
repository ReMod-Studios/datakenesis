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
    private val _cases: MutableSet<MultipartCase> = mutableSetOf()
) {
    val cases: Set<MultipartCase> by this::_cases

    constructor(init: InitFor<Scope>) : this() { ScopeImpl().init() }

    interface Scope {
        fun case(case: MultipartCase)
        fun case(init: InitFor<MultipartCase.Scope>) {
            case(MultipartCase(init))
        }
    }

    private inner class ScopeImpl: Scope {
        override fun case(case: MultipartCase) {
            _cases.add(case)
        }
    }
}

@Serializable
@DatakenesisDslMarker
data class MultipartCase(
    @SerialName("when")
    private var _when: MultipartWhen? = null,
    @SerialName("apply")
    private var _apply: Variant? = null,
) {
    constructor(init: InitFor<Scope>) : this() { ScopeImpl().init() }

    interface Scope {
        fun whenState(init: InitFor<MultipartWhen.State.Scope>)
        fun whenOr(init: InitFor<MultipartWhen.Or.Scope>)
        fun apply(model: Identifier, init: InitFor<Variant.Simple> = {})
        fun applyMultiple(init: InitFor<Variant.Multi.Scope>)
    }

     private inner class ScopeImpl: Scope {
        override fun whenState(init: InitFor<MultipartWhen.State.Scope>) {
            _when = MultipartWhen.State(init)
        }
         override fun whenOr(init: InitFor<MultipartWhen.Or.Scope>) {
            _when = MultipartWhen.Or(init)
        }
         override fun apply(model: Identifier, init: InitFor<Variant.Simple>) {
            _apply = Variant.Simple(model).apply(init)
        }
         override fun applyMultiple(init: InitFor<Variant.Multi.Scope>) {
            _apply = Variant.Multi(init)
        }
    }
}

@Serializable(with = MultipartWhen.Serializer::class)
@DatakenesisDslMarker
sealed class MultipartWhen {
    @Serializable
    @DatakenesisDslMarker
    data class Or(
        @SerialName("OR")
        private val _states: MutableSet<State> = mutableSetOf()
    ): MultipartWhen() {
        val states: Set<State> by this::_states

        constructor(init: InitFor<Scope>) : this() { ScopeImpl().init() }

        interface Scope {
            fun state(init: InitFor<State.Scope>)
        }

        private inner class ScopeImpl: Scope {
            override fun state(init: InitFor<State.Scope>) {
                _states.add(State(init))
            }
        }
    }

    @Serializable(with = State.Serializer::class)
    @DatakenesisDslMarker
    data class State(
        private val _props: MutableMap<String, String> = mutableMapOf()
    ): MultipartWhen() {
        val props: Map<String, String> by this::_props

        constructor(init: InitFor<Scope>) : this() { ScopeImpl().init() }

        interface Scope {
            infix fun String.isValue(other: String)
            infix fun String.isAnyIn(others: Collection<String>) {
                this isValue others.joinToString("|")
            }
        }

        private inner class ScopeImpl: Scope {
            override infix fun String.isValue(other: String) {
                _props[this] = other
            }
        }

        internal object Serializer: KSerializer<State> {
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

    internal object Serializer : JsonContentPolymorphicSerializer<MultipartWhen>(MultipartWhen::class) {
        override fun selectDeserializer(element: JsonElement) = when {
            element.jsonObject["OR"] is JsonArray -> Or.serializer()
            else -> State.serializer()
        }
    }
}




