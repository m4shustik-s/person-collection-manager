package client.commands


import client.invoker.Invoker
import shared.network.responses.Response

class ClientHelpCommand : Command {
    override val name = "help"
    override val description = "вывести справку по доступным командам"

    override fun execute(args: List<String?>): Response {
        var helpText = ""
        Invoker.getCommands().forEach{ (_, value) ->
            helpText += "${value.name} - ${value.description}\n"
        }
        return Response(true, helpText.removeSuffix("\n"))
    }
}
