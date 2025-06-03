package server.shared.data

class User (
    var id: Int?,
    val login: String,
    val passwordHash: String
) {
    override fun toString(): String {
        return "User(id='$id', username='$login', passwordHash='$passwordHash'"
    }
}