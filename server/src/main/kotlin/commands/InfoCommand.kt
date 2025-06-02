package server.commands

import server.collection.PersonCollectionManager
import shared.network.responses.Response

class InfoCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        return Response(true, PersonCollectionManager.getCollectionInfo())
    }
    override val name: String = "info"
    override val description: String = "вывести информацию о коллекции"
    override val argType: Pair<String?, String?> = Pair(null, null)
}

