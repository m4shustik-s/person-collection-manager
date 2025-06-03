package server.entities

import server.utils.DatabaseManager
import server.utils.OutputManager
import shared.data.Coordinates
import shared.data.Country
import shared.data.Location
import shared.data.Person
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

object PersonEntity : Entity<Person> {
    private val connection = DatabaseManager.connection!!
    override fun insert(key: String?, entity: Person, userId: Int?): Int? {
        if (userId == null || key == null) return null
        val stmt = connection.prepareStatement(
            """INSERT INTO people (
                    key,
                    id,
                    name,
                    coordinates_x,
                    coordinates_y,
                    creation_date,
                    height,
                    weight,
                    passport_id,
                    nationality,
                    location_x,
                    location_y,
                    location_z,
                    user_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id""".trimIndent()
        )
        fillPlaceholders(stmt, key, entity, userId)
        try {
            val rs = stmt.executeQuery()
            OutputManager.println(rs.toString())
            return if (rs.next()) rs.getInt("id") else return null
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    override fun update(key: String?, entity: Person, userId: Int?): Int? {
        if (userId == null) return null
        val stmt = connection.prepareStatement(
            """UPDATE people SET
                    id = ?,
                    name = ?,
                    coordinates_x = ?,
                    coordinates_y = ?,
                    creation_date = ?,
                    height = ?,
                    weight = ?,
                    passport_id = ?,
                    nationality = ?,
                    location_x = ?,
                    location_y = ?,
                    location_z = ?,
                    user_id = ?
            WHERE id = ? RETURNING id""".trimIndent()
        )
        fillPlaceholders(stmt, null, entity, userId)
        stmt.setInt(14, entity.id)
        try {
            println(stmt.toString())
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.getInt("id") else return null
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    override fun delete(key: String?, id: Int) {
        if (key == null) return
        val stmt = connection.prepareStatement("DELETE FROM people WHERE key = ?")
        stmt.setString(1, key)
        stmt.executeUpdate()
    }

    override fun getAll(): List<Pair<String, Person>> {
        val stmt = connection.prepareStatement("SELECT * FROM people")
        val rs = stmt.executeQuery()
        val list = mutableListOf<Pair<String, Person>>()
        while (rs.next()) {
            val pair = loadPersonFromDatabase(rs)
            if (pair != null) {
                pair.second.userId = rs.getInt("user_id")
                list.add(pair)
            }
        }
        stmt.close()
        return list
    }

    override fun getById(id: Int): Pair<String, Person>? {
        val stmt = connection.prepareStatement("SELECT * FROM people WHERE id = ?")
        stmt.setInt(1, id)
        val rs = stmt.executeQuery()
        return if (rs.next()) loadPersonFromDatabase(rs) else null
    }

    private fun loadPersonFromDatabase(rs: ResultSet): Pair<String, Person>? {
        try {
            return Pair(
                rs.getString("key"),
                Person(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    coordinates = Coordinates(
                        x = rs.getLong("coordinates_x"),
                        y = rs.getFloat("coordinates_y")
                    ),
                    creationDate = rs.getString("creation_date"),
                    location = if (rs.getLong("location_x") == 0L) null else Location(
                        x = rs.getLong("location_x"),
                        y = rs.getFloat("location_y"),
                        z = rs.getFloat("location_z")
                    ),
                    height = if (rs.getDouble("height") == 0.0) null else rs.getDouble("height"),
                    weight = if (rs.getDouble("weight") == 0.0) null else rs.getDouble("weight"),
                    passportID = rs.getString("passport_id"),
                    nationality = Country.valueOf(rs.getString("nationality") ?: ""),
                    userId = rs.getInt("user_id")
                )
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun fillPlaceholders(stmt: PreparedStatement, key: String?, entity: Person, userId: Int) {
        var step = 1
        if (key != null) {
            stmt.setString(1, key)
            step = 0
        }
        stmt.setInt(2 - step, entity.id)
        stmt.setString(3 - step, entity.name)
        stmt.setLong(4 - step, entity.coordinates.x)
        stmt.setFloat(5 - step, entity.coordinates.y)
        stmt.setString(6 - step, entity.creationDate)
        if (entity.height == null) stmt.setNull(7 - step, Types.DOUBLE)
        else stmt.setDouble(7 - step, entity.height)
        if (entity.weight == null) stmt.setNull(8 - step, Types.DOUBLE)
        else stmt.setDouble(8 - step, entity.weight)
        stmt.setString(9 - step, entity.passportID)
        stmt.setString(10 - step, entity.nationality.toString())
        if (entity.location == null) {
            stmt.setNull(11 - step, Types.BIGINT)
            stmt.setNull(12 - step, Types.REAL)
            stmt.setNull(13 - step, Types.REAL)
        } else {
            stmt.setLong(11 - step, entity.location.x)
            stmt.setFloat(12 - step, entity.location.y)
            stmt.setFloat(13 - step, entity.location.z)
        }
        stmt.setInt(14 - step, userId)
    }
}