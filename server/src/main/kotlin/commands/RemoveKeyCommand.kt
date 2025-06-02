package server.commands
import server.collection.PersonCollectionManager
import shared.network.responses.Response

class RemoveKeyCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val key = args[0] as String?
            ?: return Response(false, "Ключ не может быть null")
        val removed = PersonCollectionManager.removePerson(key)
        return if (removed != null) Response(true, "Элемент с ключом $key успешно удалён")
        else Response(false, "Нет элемента с ключом $key")
    }
    override val name: String = "remove_key"
    override val description: String = "удалить элемент по ключу"
    override val argType: Pair<String?, String?> = Pair("String", null)
}
