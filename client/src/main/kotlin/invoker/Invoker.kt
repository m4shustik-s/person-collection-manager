package client.invoker

import client.State
import client.commands.ClientExecuteScriptCommand
import client.commands.ClientExitCommand
import client.commands.ClientHelpCommand
import client.commands.Command
import client.ui.OutputManager

object Invoker {
    private val commands = mutableMapOf<String, Command>()

    init {
        registerCommand(ClientHelpCommand())
        registerCommand(ClientExitCommand())
        registerCommand(ClientExecuteScriptCommand())
    }

    fun registerCommand(command: Command) {
        commands[command.name] = command
    }

    fun getCommands(): Map<String, Command> = commands.toMap()

    fun executeCommand(commandName: String, args: List<String?>) {

        val command = commands[commandName] ?: run {
            OutputManager.printError("Неизвестная команда: $commandName. Введите 'help' для списка команд.")
            return
        }

        try {
            val response = command.execute(args)?.message
            if (response != null) {
                if (commandName == "exit") State.isRunning = false
                OutputManager.println(response)
            }
            else OutputManager.printError("Нет результата")
        } catch (e: Exception) {
            OutputManager.printError("Ошибка выполнения команды: ${e.message}")
        }
    }
}