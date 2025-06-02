package server.commands

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import shared.data.Person
import shared.network.responses.Response

class UpdateCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val person = Json.decodeFromJsonElement<Person>(args[1] as JsonElement)
        val success = PersonCollectionManager.updatePerson(
            (args[0] as String).toInt(),
            person
        )
        return if (success) Response(true, "Элемент успешно обновлён") else Response(false, "Элемент с таким id не найден")
    }
    override val name: String = "update"
    override val description: String = "обновить значение элемента по id"
    override val argType: Pair<String?, String?> = Pair("Int", "Person")
}

