package client.commands

import client.State
import client.ui.OutputManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import network.NetworkManager
import shared.network.commands.Request
import ui.InputManager

class ClientServerCommand(
    override val name: String,
    override val description: String,
    private val argType: Pair<String?, String?>
) : Command {
    override fun execute(args: List<String?>) {
        NetworkManager.sendRequest(Request("PING"))
        if (!State.connectedToServer) return
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
        if (!compareTypes(key?.javaClass?.typeName, argType.first)) {
            OutputManager.println("Неверный тип ключа")
            return
        }
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
        if (!compareTypes(data?.javaClass?.typeName, argType.second)) {
            OutputManager.printError("Неверный тип данных")
            return
        }

        val response = NetworkManager.sendRequest(Request(
            name,
            params
        ))
        if (response != null) OutputManager.println(response.message)
        else OutputManager.printError("Нет результата")
    }

    private fun compareTypes(provided: String?, required: String?): Boolean {
        return when (provided) {
            null -> required == null
            else -> provided.contains(required.toString())
        }
    }
}