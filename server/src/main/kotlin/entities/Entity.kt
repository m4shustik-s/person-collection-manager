package server.entities

interface Entity<T> {
    fun insert(key: String? = null, entity: T, userId: Int? = null): Int?
    fun update(key: String? = null, entity: T, userId: Int? = null): Int?
    fun delete(key: String? = null, id: Int? = null)
    fun getAll(): List<Pair<String?, T>>
    fun getById(id: Int): Pair<String?, T>?
}