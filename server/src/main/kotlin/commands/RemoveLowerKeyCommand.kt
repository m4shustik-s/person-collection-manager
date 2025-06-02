package server.commands

import server.collection.PersonCollectionManager
import shared.network.responses.Response

class RemoveLowerKeyCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val key = args[0] ?: return Response(false, "Ключ не может быть null")
        val removed = PersonCollectionManager.removeLowerKey(args[0] as String)
        return if (removed.isNotEmpty()) Response(true, "Удалено ${removed.size} элементов с ключами меньше $key")
        else Response(true, "Не найдено элементов с ключами меньше $key")
    }
    override val name: String = "remove_lower_key"
    override val description: String = "удалить элементы с ключами меньше заданного"
    override val argType: Pair<String?, String?> = Pair("String", null)
}
