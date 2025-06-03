package invoker
import server.collection.PersonCollectionManager
import server.commands.*
import server.entities.UserEntity
import shared.network.responses.Response

object Invoker {
    private val commands = mutableMapOf<String, ServerCommand>()

    init {
        registerCommand(AuthorizeCommand())
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
        registerCommand(ShowCommand())
        registerCommand(UpdateCommand())
    }

    private fun registerCommand(command: ServerCommand) {
        commands[command.name] = command
    }

    fun getCommands(): Map<String, ServerCommand> = commands.toMap()

    fun executeCommand(commandName: String, args: List<Any?>): Response? {
        if (commandName != "authorize") {
            val requestUser = PersonCollectionManager.getAllUsers().find { user ->
                user.login == args[2] as String &&
                user.passwordHash == UserEntity.hashPassword(args[3] as String)
            }
            if (requestUser == null) return Response(true, "Ошибка выполнения: пользователь не авторизан")
        }
        return commands[commandName]?.execute(args)
    }
}