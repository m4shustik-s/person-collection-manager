package server.commands
import server.collection.PersonCollectionManager
import shared.data.Location
import shared.network.responses.Response

class CountLessThanLocationCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        if (args[1] != null) {
            val count = PersonCollectionManager.countLessThanLocation(args[1] as Location)
            return Response(true, "Количество элементов с локацией меньше заданной: $count")
        } else return Response(false, "Ошибка: не передан ключ")
    }
    override val name: String =  "count_less_than_location"
    override val description: String = "вывести количество элементов с локацией меньше заданной"
    override val argType: Pair<String?, String?> = Pair(null, "Location")
}
