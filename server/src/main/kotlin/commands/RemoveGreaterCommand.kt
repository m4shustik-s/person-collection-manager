package server.commands

import server.collection.PersonCollectionManager
import shared.network.commands.RemoveGreaterRequest
import shared.network.responses.Response

class RemoveGreaterCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val removed = PersonCollectionManager.removeGreater((args[0] as RemoveGreaterRequest).element)
        return Response(true, "Удалено ${removed.size} элементов, больших чем заданный")
    }
    override val name: String = "remove_greater"
    override val description: String = "удалить элементы, превышающие заданный"
    override val argType: Pair<String?, String?> = Pair(null, "Person")
}
