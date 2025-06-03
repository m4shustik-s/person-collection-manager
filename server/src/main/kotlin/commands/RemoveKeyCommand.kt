package server.commands
import server.collection.PersonCollectionManager
import server.entities.PersonEntity
import shared.network.responses.Response

class RemoveKeyCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {


        val user = PersonCollectionManager.getAllUsers().find { user -> user.login == args[2] }
        if (user != null) {
            val key = args[0] as String?
                ?: return Response(false, "Ключ не может быть null")
            try {
                PersonEntity.delete(key)
                return Response(true, "Элемент с ключом $key успешно удалён")
            } catch (_: Exception) {
                return Response(false, "Нет элемента с ключом $key")
            }
        }
        return Response(false, "Пользователь не авторизован")
    }
    override val name: String = "remove_key"
    override val description: String = "удалить элемент по ключу"
    override val argType: Pair<String?, String?> = Pair("String", null)
}
