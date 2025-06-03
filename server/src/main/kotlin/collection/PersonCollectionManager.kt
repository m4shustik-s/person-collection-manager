package server.collection

import server.entities.PersonEntity
import server.entities.UserEntity
import server.shared.data.User
import shared.data.Location
import shared.data.Person
import java.util.concurrent.ConcurrentHashMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CopyOnWriteArrayList

object PersonCollectionManager {
    private val collection = ConcurrentHashMap<String, Person>()
    private val users = CopyOnWriteArrayList<User>()
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

    fun removePerson(key: String, userId: Int): Person? {
        if (collection[key]?.userId == userId) return collection.remove(key)
        return null
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

    fun getAll(): ConcurrentHashMap<String, Person> = collection
    fun getAllUsers(): CopyOnWriteArrayList<User> = users

    fun removeGreater(person: Person, userId: Int): List<Person> {
        val toRemove = collection.filterValues { it > person }.keys
        val removed = mutableListOf<Person>()
        for (key in toRemove) {
            val el = collection[key]
            if (el != null && el.userId == userId) {
                PersonEntity.delete(key)
                removed.add(el)
            }
        }
        return removed
    }

    fun replaceIfLower(key: String, person: Person, userId: Int): Boolean {
        val current = collection[key] ?: return false
        if (person < current && current.userId == userId) {
            PersonEntity.update(key, person)
            return true
        }
        return false
    }

    fun removeLowerKey(key: String, userId: Int): List<Person> {
        val toRemove = collection.keys.filter { it < key }
        val removed = mutableListOf<Person>()
        for (k in toRemove) {
            val el = collection[k]
            if (el != null && el.userId == userId) {
                PersonEntity.delete(k)
                removed.add(el)
            }
        }
        return removed
    }

    fun countLessThanLocation(location: Location): Int {
        return collection.values.count { it.location != null && it.location < location }
    }

    fun filterByHeight(height: Double): Map<String, Person> {
        return collection.filterValues { it.height == height }
    }

    fun getPassportIDsDescending(): List<String> {
        return collection.values.map { it.passportID }.sortedDescending()
    }

    fun loadCollection() {
        collection.clear()
        users.clear()
        PersonEntity.getAll().forEach{(key, value) ->
            collection[key] = value
        }
        UserEntity.getAll().forEach{(_, value) ->
            users.add(value)
        }
    }
}
