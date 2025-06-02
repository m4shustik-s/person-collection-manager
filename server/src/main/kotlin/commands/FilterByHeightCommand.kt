package server.commands
import server.collection.PersonCollectionManager
import shared.network.responses.Response

class FilterByHeightCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val filtered = PersonCollectionManager.filterByHeight((args[1] as Double))
        return if (filtered.isEmpty()) Response(true, "Нет элементов с указанным ростом")
        else Response(true, filtered.values.joinToString("\n"))
    }
    override val name: String = "filter_by_height"
    override val description: String = "вывести элементы с указанным ростом"
    override val argType: Pair<String?, String?> = Pair(null, "Double")
}
