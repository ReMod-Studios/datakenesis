package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@DatakenesisDslMarker
data class Multipart(
    @SerialName("multipart")
    val cases: Set<MultipartCase>
)

inline fun multipart(init: InitFor<MultipartBuilder>) = MultipartBuilder().apply(init).build()

@DatakenesisDslMarker
class MultipartBuilder: Builder<Multipart> {
    val cases: MutableSet<MultipartCase> = mutableSetOf()

    override fun build() = Multipart(cases)

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
class MultipartCaseBuilder: Builder<MultipartCase> {
    var whenCriteria: MultipartWhen? = null
    var apply: Variant? = null

    override fun build() = MultipartCase(
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
    inline fun apply(init: InitFor<Variant.MultiBuilder>) {
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
        fun build() = Or(states)

        inline fun add(init: InitFor<StateBuilder>) {
            states.add(StateBuilder().apply(init).build())
        }
    }

    @Serializable(with = MultipartWhenStateSerializer::class)
    data class State(val states: Map<String, String>): MultipartWhen

    @DatakenesisDslMarker
    class StateBuilder: Builder<State> {
        val props: MutableMap<String, String> = mutableMapOf()

        override fun build() = State(props)

        fun expect(prop: String, vararg states: String) {
            props[prop] = states.joinToString("|")
        }
    }
}


