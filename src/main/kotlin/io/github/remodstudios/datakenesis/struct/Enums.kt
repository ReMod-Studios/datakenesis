package io.github.remodstudios.datakenesis.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Axis {
    @SerialName("x") X,
    @SerialName("y") Y,
    @SerialName("z") Z
}

@Serializable
enum class Direction {
    @SerialName("down") DOWN,
    @SerialName("up") UP,
    @SerialName("north") NORTH,
    @SerialName("south") SOUTH,
    @SerialName("east") EAST,
    @SerialName("west") WEST,
}

@Serializable
enum class Position {
    @SerialName("thirdperson_righthand") THIRDPERSON_RIGHTHAND,
    @SerialName("thirdperson_lefthand") THIRDPERSON_LEFTHAND,
    @SerialName("firstperson_righthand") FIRSTPERSON_RIGHTHAND,
    @SerialName("firstperson_lefthand") FIRSTPERSON_LEFTHAND,
    @SerialName("gui") GUI,
    @SerialName("head") HEAD,
    @SerialName("ground") GROUND,
    @SerialName("fixed") FIXED,
}