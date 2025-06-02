package shared.network.commands

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Request(
    val command: String,
    val args: Map<String, JsonElement> = emptyMap()
)