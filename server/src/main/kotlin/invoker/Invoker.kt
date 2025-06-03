package invoker
import server.commands.*
import shared.network.responses.Response

object Invoker {
    private val commands = mutableMapOf<String, ServerCommand>()

    init {
        registerCommand(CountLessThanLocationCommand())
        registerCommand(FilterByHeightCommand())
        registerCommand(InfoCommand())
        registerCommand(InsertCommand())
        registerCommand(LoadCommandsCommand())
        registerCommand(PrintFieldDescendingPassportIDCommand())
        registerCommand(RemoveGreaterCommand())
        registerCommand(RemoveKeyCommand())
        registerCommand(RemoveLowerKeyCommand())
        registerCommand(ReplaceIfLowerCommand())
        registerCommand(SaveCommand())
        registerCommand(ShowCommand())
        registerCommand(UpdateCommand())
    }

    private fun registerCommand(command: ServerCommand) {
        commands[command.name] = command
    }

    fun getCommands(): Map<String, ServerCommand> = commands.toMap()

    fun executeCommand(commandName: String, args: List<Any?>): Response? {
        return commands[commandName]?.execute(args)
    }
}