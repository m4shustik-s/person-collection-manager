package server.utils

import server.State
import shared.data.Country
import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {
    private val db_host = "192.168.1.101"
    private val db_port = 5432
    private val db_name = "postgres"
    private val db_user = "goida"
    private val db_pass = "goida"
    private val url = "jdbc:postgresql://$db_host:$db_port/$db_name"
    var connection: Connection? = null

    fun setUp() {
        try {
            connection = DriverManager.getConnection(url, db_user, db_pass)
            OutputManager.println("Connected to database")

            val statement = connection!!.createStatement()
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users(
                    id SERIAL PRIMARY KEY NOT NULL,
                    login VARCHAR(100) NOT NULL,
                    password_hash VARCHAR(255) NOT NULL
                )
            """.trimIndent())
            OutputManager.println("Таблица 'users' успешно создана / уже существует")
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS people(
                    key VARCHAR(100) PRIMARY KEY NOT NULL,
                    id INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    coordinates_x BIGINT NOT NULL,
                    coordinates_y REAL NOT NULL,
                    creation_date VARCHAR(100) NOT NULL,
                    height DOUBLE PRECISION,
                    weight DOUBLE PRECISION,
                    passport_id VARCHAR(100) NOT NULL,
                    nationality VARCHAR(14) NOT NULL,
                    location_x BIGINT,
                    location_y REAL,
                    location_z REAL,
                    user_id INT NOT NULL,
                    
                    CHECK (id > 0),
                    CHECK (height IS NULL OR (height IS NOT NULL AND height > 0)),
                    CHECK (weight IS NULL OR (weight IS NOT NULL AND weight > 0)),
                    CHECK (nationality IN (
                        '${Country.UNITED_KINGDOM}',
                        '${Country.SOUTH_KOREA}',
                        '${Country.ITALY}',
                        '${Country.CHINA}'
                    )),
                    CHECK (
                       (
                        location_x IS NULL AND
                        location_y IS NULL AND
                        location_z IS NULL
                       ) OR
                       (
                        location_x IS NOT NULL AND
                        location_y IS NOT NULL AND
                        location_z IS NOT NULL
                       )
                    ),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """.trimIndent())
            OutputManager.println("Таблица 'people' успешно создана / уже существует")
            State.connectedToDatabase = true
        } catch (e: Exception) {
            OutputManager.println("Нет подключения к базе данных, переподключение через 1 сек")
        }
    }
}