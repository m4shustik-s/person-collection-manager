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
        val data: Any?
        val params: MutableMap<String, JsonElement> = mutableMapOf()
        var key: Any? = null
        when (argType.first) {
            "String" -> {
                key = args[0]
                params["key"] = json.encodeToJsonElement(key)
            }
            "Int" -> {
                key = args[0]?.toInt()
                params["key"] = json.encodeToJsonElement(key)
            }
        }
        if (!compareTypes(key?.javaClass?.typeName, argType.first))
            return Response(false, "Неверный тип ключа")
        when (argType.second) {
            "Person" -> {
                data = InputManager.readPerson(setOf())
                params["data"] = json.encodeToJsonElement(data)
            }

            "Location" -> {
                data = InputManager.readLocation()
                params["data"] = json.encodeToJsonElement(data)
            }

            else -> {
                if (key == null) {
                    data = args[0]
                    params["data"] = json.encodeToJsonElement(data)
                } else data = null
            }
        }
        if (!compareTypes(data?.javaClass?.typeName, argType.second))
            return Response(false, "Неверный тип данных")

        return NetworkManager.sendRequest(Request(
            name,
            params
        ))
    }

    private fun compareTypes(provided: String?, required: String?): Boolean {
        println("$provided -- $required")
        return when (provided) {
            null -> required == null
            else -> provided.contains(required.toString())
        }
    }
}