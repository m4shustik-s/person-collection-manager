package invoker
import server.commands.*
import shared.network.responses.Response

object Invoker {
    private val commands = mutableMapOf<String, ServerCommand>()

    init {
        registerCommand(InfoCommand())
        registerCommand(InsertCommand())
        registerCommand(LoadCommandsCommand())
        registerCommand(ShowCommand())
    }

    fun registerCommand(command: ServerCommand) {
        commands[command.name] = command
    }

    fun getCommands(): Map<String, ServerCommand> = commands.toMap()

    fun executeCommand(commandName: String, args: List<Any?>): Response? {
        return commands[commandName]?.execute(args)
    }
}