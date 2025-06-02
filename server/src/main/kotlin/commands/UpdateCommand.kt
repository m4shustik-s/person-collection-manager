package server.commands

import server.collection.PersonCollectionManager
import shared.data.Person
import shared.network.responses.Response

class UpdateCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val success = PersonCollectionManager.updatePerson(
            (args[1] as Person).id,
            (args[1] as Person)
        )
        return if (success) Response(true, "Элемент успешно обновлён") else Response(false, "Элемент с таким id не найден")
    }
    override val name: String = "update"
    override val description: String = "обновить значение элемента по id"
    override val argType: Pair<String?, String?> = Pair(null, "Person")
}

