package client.commands

import client.State
import client.ui.OutputManager

class ClientExitCommand : Command {
    override val name = "exit"
    override val description = "завершить программу"

    override fun execute(args: List<String?>) {
        State.isRunning = false
        OutputManager.println("Завершение работы клиента")
    }
}

