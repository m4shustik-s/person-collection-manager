package server.commands

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import shared.data.Person
import shared.network.responses.Response

class RemoveGreaterCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val user = PersonCollectionManager.getAllUsers().find { user -> user.login == args[2] }
        if (user != null) {
            val person = Json.decodeFromJsonElement<Person>(args[1] as JsonElement)
            val removed = PersonCollectionManager.removeGreater(
                person,
                user.id!!
            )
            return Response(true, "Удалено ${removed.size} элементов, больших, чем заданный")
        }
        return Response(false, "Пользователь не авторизован")
    }
    override val name: String = "remove_greater"
    override val description: String = "удалить элементы, превышающие заданный"
    override val argType: Pair<String?, String?> = Pair(null, "Person")
}
