package server.commands

import server.collection.PersonCollectionManager
import shared.network.responses.Response

class ClearCommand : ServerCommand {
    override val name = "clear"
    override val description = "очистить коллекцию"
    override val argType: Pair<String?, String?> = Pair(null, null)

    override fun execute(args: List<Any?>): Response {
        PersonCollectionManager.clearCollection()
        return Response(true, "Коллекция успешно очищена")
    }
}


