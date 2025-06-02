package server.collection

import shared.data.Location
import shared.data.Person
import java.util.concurrent.ConcurrentHashMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PersonCollectionManager {
    private val collection = ConcurrentHashMap<String, Person>()

    private val initializationDate: LocalDateTime = LocalDateTime.now()

    fun addPerson(key: String, person: Person): Boolean {
        if (collection.containsKey(key)) return false
        if (collection.values.any { it.passportID == person.passportID }) return false
        collection[key] = person
        return true
    }

    fun updatePerson(id: Int, person: Person): Boolean {
        val entry = collection.entries.find { it.value.id == id } ?: return false
        collection[entry.key] = person
        return true
    }

    fun removePerson(key: String): Person? {
        return collection.remove(key)
    }

    fun clearCollection() {
        collection.clear()
    }

    fun getCollectionInfo(): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val formattedDate = initializationDate.format(formatter)
        return "Тип коллекции: ${collection::class.simpleName}\n" +
                "Количество элементов: ${collection.size}\n" +
                "Дата инициализации: $formattedDate"
    }

    fun getAll(): Collection<Person> = collection.values

    fun removeGreater(person: Person): List<Person> {
        val toRemove = collection.filterValues { it > person }.keys
        val removed = mutableListOf<Person>()
        for (key in toRemove) {
            collection.remove(key)?.let { removed.add(it) }
        }
        return removed
    }

    fun replaceIfLower(key: String, person: Person): Boolean {
        val current = collection[key] ?: return false
        if (person < current) {
            collection[key] = person
            return true
        }
        return false
    }

    fun removeLowerKey(key: String): List<Person> {
        val toRemove = collection.keys.filter { it < key }
        val removed = mutableListOf<Person>()
        for (k in toRemove) {
            collection.remove(k)?.let { removed.add(it) }
        }
        return removed
    }

    fun countLessThanLocation(location: Location): Int {
        return collection.values.count { it.location != null && it.location!! < location }
    }

    fun filterByHeight(height: Double): Map<String, Person> {
        return collection.filterValues { it.height == height }
    }

    fun getPassportIDsDescending(): List<String> {
        return collection.values.map { it.passportID }.sortedDescending()
    }
}
