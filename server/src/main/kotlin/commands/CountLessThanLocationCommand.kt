package server.commands
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import server.collection.PersonCollectionManager
import shared.data.Location
import shared.network.responses.Response

class CountLessThanLocationCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        if (args[1] != null) {
            val location = Json.decodeFromJsonElement<Location>(args[1] as JsonElement)
            val count = PersonCollectionManager.countLessThanLocation(location)
            return Response(true, "Количество элементов с локацией меньше заданной: $count")
        } else return Response(false, "Ошибка: не передан ключ")
    }
    override val name: String =  "count_less_than_location"
    override val description: String = "вывести количество элементов с локацией меньше заданной"
    override val argType: Pair<String?, String?> = Pair(null, "Location")
}
