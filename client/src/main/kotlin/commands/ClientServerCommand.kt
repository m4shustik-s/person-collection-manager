package client.commands

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import network.NetworkManager
import shared.network.commands.Request
import shared.network.responses.Response
import ui.InputManager

class ClientServerCommand(
    override val name: String,
    override val description: String,
    private val argType: Pair<String?, String?>
) : Command {
    override fun execute(args: List<String?>): Response? {
        val json = Json { ignoreUnknownKeys = true}
        val params: Map<String, JsonElement>
        var keyType: String? = null
        var dataType: String? = null
        when (name) {
            "insert", "update" -> {
                params = mapOf(
                    "key" to json.encodeToJsonElement(args[0]),
                    "data" to json.encodeToJsonElement(InputManager.readPerson(setOf()))
                )
                keyType = "String"
                dataType = "Person"
            }

            "count_less_than_location" -> {
                params = mapOf("data" to json.encodeToJsonElement(InputManager.readLocation()))
                dataType = "Location"
            }

            else -> {
                params = mapOf("key" to json.encodeToJsonElement(args[0]))
                keyType = if (args[0] != null) "String" else null
            }
        }
        return if (compareTypes(keyType, argType.first) && compareTypes(dataType, argType.second)) {
            NetworkManager.sendRequest(Request(
                name,
                params
            ))
        } else {
            null
        }
    }

    private fun compareTypes(provided: String?, required: String?): Boolean {
        return when (provided) {
            null -> required == null
            else -> provided.contains(required.toString())
        }
    }
}