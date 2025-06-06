package ui

import client.State
import client.invoker.Invoker
import client.ui.OutputManager
import network.NetworkManager
import shared.data.Person
import shared.data.Coordinates
import shared.data.Country
import shared.data.Location
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object InputManager {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(System.`in`))

    private fun readLine(prompt: String = ""): String? {
        OutputManager.print(prompt)
        return try {
            reader.readLine()?.trim()?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }
    fun readInt(prompt: String, min: Int? = null, max: Int? = null): Int {
        while (true) {
            val input = readLine(prompt) ?: throw IllegalArgumentException("Input cannot be empty")
            try {
                val value = input.toInt()
                min?.let { if (value < it) throw IllegalArgumentException("Value must be >= $min") }
                max?.let { if (value > it) throw IllegalArgumentException("Value must be <= $max") }
                return value
            } catch (e: NumberFormatException) {
                OutputManager.printError("Please enter a valid integer.")
            } catch (e: IllegalArgumentException) {
                OutputManager.printError("${e.message}")
            }
        }
    }

    fun readPerson(passportIds: Set<String>, id: Int? = null): Person {
        val name = readNonEmptyString("Enter name: ")
        val coordinates = readCoordinates()
        val height = readNullableDouble("Enter height (or empty for null): ", 0.0)
        val weight = readNullableDouble("Enter weight (or empty for null): ", 0.0)
        val passportID = readUniquePassportID("Enter passport ID: ", passportIds)
        val nationality = readNationality()
        val location = readLocation()

        return Person(
            id = id ?: Person.generateId(),
            name = name,
            coordinates = coordinates,
            creationDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            height = height,
            weight = weight,
            passportID = passportID,
            nationality = nationality,
            location = location
        )
    }

    private fun readNonEmptyString(prompt: String): String {
        while (true) {
            val input = readLine(prompt) ?: throw IllegalArgumentException("Input cannot be empty")
            if (input.isNotEmpty()) return input
            OutputManager.printError("This field cannot be empty.")
        }
    }

    private fun readCoordinates(): Coordinates {
        OutputManager.println("Enter coordinates:")
        val x = readLong("  x: ")
        val y = readFloat("  y: ")
        return Coordinates(x, y)
    }

    private fun readNullableDouble(prompt: String, min: Double): Double? {
        while (true) {
            val input = readLine(prompt) ?: return null
            try {
                val value = input.toDouble()
                if (value <= min) throw IllegalArgumentException("Value must be > $min")
                return value
            } catch (e: NumberFormatException) {
                OutputManager.printError("Please enter a valid number or empty for null.")
            } catch (e: IllegalArgumentException) {
                OutputManager.printError("${e.message}")
            }
        }
    }

    private fun readUniquePassportID(prompt: String, existingIds: Set<String>): String {
        while (true) {
            val input = readNonEmptyString(prompt)
            if (!existingIds.contains(input)) return input
            OutputManager.printError("Passport ID must be unique. This ID already exists.")
        }
    }

    private fun readNationality(): Country {
        OutputManager.println("Available nationalities: ${Country.values().joinToString(", ") { it.name }}")
        while (true) {
            val input = readNonEmptyString("Enter nationality: ")
            try {
                return Country.valueOf(input.uppercase())
            } catch (e: IllegalArgumentException) {
                OutputManager.printError("Invalid nationality. Please choose from the available options.")
            }
        }
    }

    fun needToReconnect() {
        while (!State.connectedToServer) {
            OutputManager.println("Соединение разорвано, переподключиться? (Y/n): ")
            OutputManager.print("> ")
            val input = readlnOrNull()
            when (input?.trim()) {
                "Y" -> {
                    NetworkManager.healthCheck()
                    break
                }
                "n" -> {
                    Invoker.executeCommand("exit", listOf())
                    break
                }
            }
        }
    }

    fun getAuthCredentials(msg: String? = null) {
        State.login = null
        State.password = null
        while (State.login.isNullOrBlank() || State.password.isNullOrBlank()) {
            if (msg != null) OutputManager.println(msg)
            if (State.login.isNullOrBlank()) {
                OutputManager.println("Введите Ваш логин:")
                OutputManager.print("> ")
                State.login = readlnOrNull()
            }
            if (State.password.isNullOrBlank()) {
                OutputManager.println("Введите Ваш пароль:")
                OutputManager.print("> ")
                State.password = readlnOrNull()
            }
        }
        NetworkManager.healthCheck()
    }

    /**
     * Get IPv4 address of server from user
     */
    fun getServerAddress() {
        while (State.host == null) {
            OutputManager.println("Specify server ipv4 address:")
            OutputManager.print("> ")
            val input = readln()
            if (isValidIPv4(input)) State.host = input
            else OutputManager.printError("Incorrect IPv4 address.")
        }
    }

    /**
     * Checks if provided IP is valid for IPv4
     * @return true, if ip is valid, false otherwise
     */
    private fun isValidIPv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (ip.trim() == "localhost") return true
        if (parts.size != 4) return false
        for (part in parts) {
            if (part.isEmpty() || part.length > 3) return false
            if (part.length > 1 && part.startsWith('0')) return false
            val num = part.toIntOrNull() ?: return false
            if (num !in 0..255) return false
        }
        return true
    }

    fun readLocation(): Location? {
        OutputManager.println("Enter location (leave empty for null):")
        return try {
            val x = readLong("  x: ")
            val y = readFloat("  y: ")
            val z = readFloat("  z: ")
            Location(x, y, z)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun readLong(prompt: String): Long {
        while (true) {
            val input = readLine(prompt) ?: throw IllegalArgumentException("Input cannot be empty")
            try {
                return input.toLong()
            } catch (e: NumberFormatException) {
                OutputManager.printError("Please enter a valid long number.")
            }
        }
    }

    private fun readFloat(prompt: String): Float {
        while (true) {
            val input = readLine(prompt) ?: throw IllegalArgumentException("Input cannot be empty")
            try {
                return input.toFloat()
            } catch (e: NumberFormatException) {
                OutputManager.printError("Please enter a valid float number.")
            }
        }
    }
}