package server.commands
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import server.entities.PersonEntity
import shared.data.Person
import shared.network.responses.Response

class ReplaceIfLowerCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val user = PersonCollectionManager.getAllUsers().find { user -> user.login == args[2] }
        if (user != null) {
            val key = args[0] ?: return Response(false, "Ключ не может быть null")
            val existedPerson = PersonEntity.getById((args[0] as String).toInt())
            val owner = PersonCollectionManager.getAllUsers().find { us ->
                us.id == existedPerson?.second?.userId
            }
            if (owner?.login == args[2]) {
                val person = Json.decodeFromJsonElement<Person>(args[1] as JsonElement)
                val result = PersonCollectionManager.replaceIfLower(args[0] as String, person, user.id!!)
                return if (result) Response(true, "Элемент с ключом $key успешно заменён (новое значение меньше старого)")
                else Response(false, "Элемент не заменён (новое значение не меньше старого или ключ не найден)")

            }
            return Response(true, "Только владелец может изменять свой объект")
        }
        return Response(false, "Пользователь не авторизован")
    }
    override val name: String = "replace_if_lower"
    override val description: String = "заменить значение по ключу, если новое значение меньше старого"
    override val argType: Pair<String?, String?> = Pair("String", "Person")
}
