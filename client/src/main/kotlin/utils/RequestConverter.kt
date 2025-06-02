package client.utils

import shared.network.commands.CommandRequest
import shared.network.commands.Request
import kotlinx.serialization.json.*

object RequestConverter {
    private val json = Json { ignoreUnknownKeys = true }

    fun toNetworkRequest(commandRequest: CommandRequest): Request {
        val jsonElement = json.encodeToJsonElement(commandRequest)
        val args = if (jsonElement is JsonObject) {
            jsonElement.toMap() - "commandName"
        } else emptyMap()
        return Request(command = commandRequest.commandName, args = args)
    }
}
