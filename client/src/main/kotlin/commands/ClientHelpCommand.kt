package client.commands


import client.invoker.Invoker
import client.ui.OutputManager

class ClientHelpCommand : Command {
    override val name = "help"
    override val description = "вывести справку по доступным командам"

    override fun execute(args: List<String?>) {
        var helpText = ""
        Invoker.getCommands().forEach{ (_, value) ->
            helpText += "${value.name} - ${value.description}\n"
        }
        OutputManager.println(helpText.removeSuffix("\n"))
    }
}
