package shared.data

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    var id: Int,
    val name: String,
    val coordinates: Coordinates,
    val creationDate: String,
    val height: Double?,
    val weight: Double?,
    val passportID: String,
    val nationality: Country,
    val location: Location?,
    var userId: Int = -1
) : Comparable<Person> {
    init {
        require(id > 0) { "ID must be greater than 0" }
        require(name.isNotEmpty()) { "Name cannot be empty" }
        height?.let { require(it > 0) { "Height must be greater than 0 if not null" } }
        weight?.let { require(it > 0) { "Weight must be greater than 0 if not null" } }
        require(passportID.isNotEmpty()) { "Passport ID cannot be empty" }
    }

    override fun compareTo(other: Person): Int = id.compareTo(other.id)

    override fun toString(): String {
        return """
            |  id: $id,
            |  name: '$name',
            |  coordinates: $coordinates,
            |  creationDate: $creationDate,
            |  height: ${height ?: "null"},
            |  weight: ${weight ?: "null"},
            |  passportID: '$passportID',
            |  nationality: $nationality,
            |  location: ${location ?: "null"}
            |""".trimMargin()
    }

    companion object {
        private var maxId: Int = 0

        fun generateId(existingIds: Collection<Int> = emptyList()): Int {
            maxId = maxOf(maxId, existingIds.maxOrNull() ?: 0)
            return ++maxId
        }
    }
}