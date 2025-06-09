package server.commands

import server.collection.PersonCollectionManager
import server.entities.PersonEntity
import shared.network.responses.Response

class RemoveLowerKeyCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val user = PersonCollectionManager.getAllUsers().find { user -> user.login == args[2] }
        if (user != null) {
            val key = args[0] ?: return Response(false, "Ключ не может быть null")
            val existedPerson = PersonEntity.getById((args[0] as String).toInt())
            val owner = PersonCollectionManager.getAllUsers().find { us ->
                us.id == existedPerson?.second?.userId
            }
            if (owner?.login == args[2]) {
                val removed = PersonCollectionManager.removeLowerKey(args[0] as String, user.id!!)
                return if (removed.isNotEmpty()) Response(true, "Удалено ${removed.size} элементов с ключами меньше $key")
                else Response(true, "Не найдено элементов с ключами меньше $key")
            }
            return Response(true, "Только владелец может изменять свой объект")
        }
        return Response(false, "Пользователь не авторизован")
    }
    override val name: String = "remove_lower_key"
    override val description: String = "удалить элементы с ключами меньше заданного"
    override val argType: Pair<String?, String?> = Pair("String", null)
}
