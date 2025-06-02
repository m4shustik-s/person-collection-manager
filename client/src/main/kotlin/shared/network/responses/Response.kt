package shared.network.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Response(
    val success: Boolean,
    val message: String,
    val data: JsonElement? = null
)