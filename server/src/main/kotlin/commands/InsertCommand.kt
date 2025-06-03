package server.commands

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import server.entities.PersonEntity
import shared.data.Person
import shared.network.responses.Response

class InsertCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        if (args[0] == null) return Response(false, "Ошибка: не передан ключ")
        if (args[1] == null) return Response(false, "Ошибка: не передан или неверен объект Person")
        val data = Json.decodeFromJsonElement<Person>(args[1] as JsonElement)
        val ownerId = PersonCollectionManager.getAllUsers().find { user ->
            user.login == args[2] as String
        }?.id
        if (ownerId == null) return Response(true, "Пользователь не найден")
        val personId = PersonEntity.insert(args[0] as String?, data, ownerId)
        return if (personId != null) Response(true, "Person добавлен с ключом ${args[0]}")
        else Response(false, "Person с таким ключом или passportID уже существует")
    }
    override val name: String = "insert"
    override val description: String = "добавить новый элемент с заданным ключом"
    override val argType: Pair<String?, String?> = Pair("String", "Person")
}
