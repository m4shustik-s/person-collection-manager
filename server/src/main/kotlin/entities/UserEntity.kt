package server.entities

import server.shared.data.User
import server.utils.DatabaseManager
import java.security.MessageDigest

object UserEntity : Entity<User> {
    private val connection = DatabaseManager.connection!!
    override fun insert(key: String?, entity: User, userId: Int?): Int? {
        val stmt = connection.prepareStatement(
            "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id"
        )
        stmt.setString(1, entity.login)
        stmt.setString(2, entity.passwordHash)
        val rs = stmt.executeQuery()
        return if (rs.next()) rs.getInt("id") else return null
    }

    override fun update(key: String?, entity: User, userId: Int?): Int? {
        if (entity.id != null) {
            val stmt = connection.prepareStatement(
                "UPDATE users SET login = ?, password_hash = ? WHERE id = ?"
            )
            stmt.setString(1, entity.login)
            stmt.setString(2, entity.passwordHash)
            stmt.setInt(3, entity.id!!)
            stmt.executeUpdate()
        }
        return null
    }

    override fun delete(key: String?, id: Int) {
        val stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?")
        stmt.setInt(1, id)
        stmt.executeUpdate()
    }

    override fun getAll(): List<Pair<Nothing?, User>> {
        val stmt = connection.prepareStatement("SELECT * FROM users")
        val rs = stmt.executeQuery()
        val list = mutableListOf<Pair<Nothing?, User>>()
        while (rs.next()) {
            list.add(Pair(
                null,
                User(
                    id = rs.getInt("id"),
                    login = rs.getString("login"),
                    passwordHash = rs.getString("password_hash")
                )
            ))
        }
        stmt.close()
        return list
    }

    override fun getById(id: Int): Pair<Nothing?, User>? {
        val stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
        stmt.setInt(1, id)
        val rs = stmt.executeQuery()
        return if (rs.next())
            Pair(
                null,
                User(
                    id = rs.getInt("id"),
                    login = rs.getString("login"),
                    passwordHash = rs.getString("password_hash")
                )
            ) else null
    }

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it)}
    }
}