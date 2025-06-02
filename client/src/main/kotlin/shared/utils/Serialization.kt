package shared.utils

import shared.network.commands.Request
import client.ui.OutputManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


object Serialization {
    private val module = SerializersModule {
        polymorphic(Request::class)
    }
    val jsonFormat = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = module
    }

    inline fun <reified T> encodeToString(value: T): String {
        return try {
            jsonFormat.encodeToString(value)
        } catch (e: SerializationException) {
            throw IllegalStateException("Serialization failed: ${e.message}")
        }
    }

    inline fun <reified T> decodeFromString(json: String): T? {
        return try {
            jsonFormat.decodeFromString<T>(json)
        } catch (e: SerializationException) {
            OutputManager.println("Deserialization failed: ${e.message}")
            return null
        }
    }
}