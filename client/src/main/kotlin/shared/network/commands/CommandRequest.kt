package shared.network.commands

import kotlinx.serialization.Serializable
import shared.data.Location

@Serializable
sealed class CommandRequest {
    abstract val commandName: String
}

@Serializable
data class ClearRequest(
    override val commandName: String = "clear"
) : CommandRequest()

@Serializable
data class HelpRequest(override val commandName: String = "help") : CommandRequest()

@Serializable
data class InfoRequest(override val commandName: String = "info") : CommandRequest()

@Serializable
data class ShowRequest(override val commandName: String = "show") : CommandRequest()

@Serializable
data class InsertRequest(
    val key: String?,
    val element: shared.data.Person,
    override val commandName: String = "insert"
) : CommandRequest()

@Serializable
data class UpdateRequest(
    val id: Int,
    val element: shared.data.Person,
    override val commandName: String = "update"
) : CommandRequest()

@Serializable
data class RemoveKeyRequest(
    val key: String?,
    override val commandName: String = "remove_key"
) : CommandRequest()


@Serializable
data class ExecuteScriptRequest(
    val fileName: String,
    override val commandName: String = "execute_script"
) : CommandRequest()

@Serializable
data class ExitRequest(override val commandName: String = "exit") : CommandRequest()

@Serializable
data class RemoveGreaterRequest(
    val element: shared.data.Person,
    override val commandName: String = "remove_greater"
) : CommandRequest()

@Serializable
data class ReplaceIfLowerRequest(
    val key: String?,
    val element: shared.data.Person,
    override val commandName: String = "replace_if_lower"
) : CommandRequest()

@Serializable
data class RemoveLowerKeyRequest(
    val key: String?,
    override val commandName: String = "remove_lower_key"
) : CommandRequest()

@Serializable
data class CountLessThanLocationRequest(
    val location: Location,
    override val commandName: String = "count_less_than_location"
) : CommandRequest()

@Serializable
data class FilterByHeightRequest(
    val height: Double,
    override val commandName: String = "filter_by_height"
) : CommandRequest()

@Serializable
data class PrintFieldDescendingPassportIDRequest(
    override val commandName: String = "print_field_descending_passport_i_d"
) : CommandRequest()
