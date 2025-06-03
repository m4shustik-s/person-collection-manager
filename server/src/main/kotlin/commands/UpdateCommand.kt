package server.commands

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import server.entities.PersonEntity
import shared.data.Person
import shared.network.responses.Response

class UpdateCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val person = Json.decodeFromJsonElement<Person>(args[1] as JsonElement)
        val existedPerson = PersonEntity.getById((args[0] as String).toInt())
        val owner = PersonCollectionManager.getAllUsers().find { user ->
            user.id == existedPerson?.second?.userId
        }
        if (owner?.login == args[2]) {
            person.id = (args[0] as String).toInt()
            val personId = PersonEntity.update(null, person, owner!!.id)
            return if (personId != null) Response(true, "Элемент успешно обновлён")
            else Response(false, "Элемент с таким id не найден")
        }
        return Response(false, "Только владелец может изменять свой объект")
    }
    override val name: String = "update"
    override val description: String = "обновить значение элемента по id"
    override val argType: Pair<String?, String?> = Pair("Int", "Person")
}

