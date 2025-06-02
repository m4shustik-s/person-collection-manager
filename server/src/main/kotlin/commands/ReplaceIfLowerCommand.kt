package server.commands
import server.collection.PersonCollectionManager
import shared.data.Person
import shared.network.commands.ReplaceIfLowerRequest
import shared.network.responses.Response

class ReplaceIfLowerCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val key = args[0] ?: return Response(false, "Ключ не может быть null")
        val result = PersonCollectionManager.replaceIfLower(args[0] as String, args[1] as Person)
        return if (result) Response(true, "Элемент с ключом $key успешно заменён (новое значение меньше старого)")
        else Response(false, "Элемент не заменён (новое значение не меньше старого или ключ не найден)")
    }
    override val name: String = "replace_if_lowe"
    override val description: String = "заменить значение по ключу, если новое значение меньше старого"
    override val argType: Pair<String?, String?> = Pair("String", "Person")
}
