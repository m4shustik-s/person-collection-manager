package server.commands
import server.collection.PersonCollectionManager
import shared.network.responses.Response

class PrintFieldDescendingPassportIDCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val passportIDs = PersonCollectionManager.getPassportIDsDescending()
        return if (passportIDs.isEmpty()) Response(true, "Коллекция пуста")
        else Response(true, passportIDs.joinToString("\n"))
    }
    override val name: String = "print_field_descending_passport_i_d"
    override val description: String = "вывести passportID в порядке убывания"
    override val argType: Pair<String?, String?> = Pair(null, null)
}
