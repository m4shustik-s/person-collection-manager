package server.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import shared.data.Person
import java.nio.file.Files
import java.nio.file.Paths

object FileManager {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    fun readCollection(filePath: String): Map<Int, Person>? {
        return try {
            val json = Files.readString(Paths.get(filePath))
            val type = object : TypeToken<Map<Int, Person>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            null
        }
    }

    fun writeCollection(filePath: String, data: Map<String, Person>): Boolean {
        return try {
            Files.writeString(Paths.get(filePath), gson.toJson(data))
            true
        } catch (e: Exception) {
            false
        }
    }
}