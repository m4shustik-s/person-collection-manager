package server.commands
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import shared.network.responses.Response

class FilterByHeightCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        if (args[1] != null) {
            val height = Json.decodeFromJsonElement<Double>(args[1] as JsonElement)
            val filtered = PersonCollectionManager.filterByHeight(height)
            return if (filtered.isEmpty()) Response(true, "Нет элементов с указанным ростом")
            else Response(true, filtered.values.joinToString("\n"))
        } else return Response(true, "Недостаточно аргументов")
    }
    override val name: String = "filter_by_height"
    override val description: String = "вывести элементы с указанным ростом"
    override val argType: Pair<String?, String?> = Pair(null, "String")
}
