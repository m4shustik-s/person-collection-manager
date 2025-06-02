package shared.data

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val x: Long,
    val y: Float
) : Comparable<Coordinates> {
    override fun compareTo(other: Coordinates): Int {
        val xCompare = x.compareTo(other.x)
        return if (xCompare != 0) xCompare else y.compareTo(other.y)
    }

    override fun toString(): String = "($x, $y)"
}