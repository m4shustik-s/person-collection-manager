package shared.data

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val x: Long,
    val y: Float,
    val z: Float
) : Comparable<Location> {
    override fun compareTo(other: Location): Int {
        val xCompare = x.compareTo(other.x)
        if (xCompare != 0) return xCompare

        val yCompare = y.compareTo(other.y)
        if (yCompare != 0) return yCompare

        return z.compareTo(other.z)
    }

    override fun toString(): String = "($x, $y, $z)"
}
