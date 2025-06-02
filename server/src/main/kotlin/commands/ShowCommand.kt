package server.commands

import server.collection.PersonCollectionManager
import shared.network.responses.Response

class ShowCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val all = PersonCollectionManager.getAll()
        return if (all.isEmpty()) Response(true, "Коллекция пуста")
        else Response(true, all.joinToString("\n"))
    }
    override val name: String = "show"
    override val description: String = "вывести все элементы коллекции"
    override val argType: Pair<String?, String?> = Pair(null, null)
}
