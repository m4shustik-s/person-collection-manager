package client

import client.invoker.Invoker
import client.ui.OutputManager
import network.NetworkManager
import shared.network.commands.Request
import java.util.*

object State {
    var isRunning = false
    var connectedToServer = false
}

fun main() {
    val scanner = Scanner(System.`in`)
    println("Клиент запущен. Введите команду (help для списка):")
    State.isRunning = true
    NetworkManager.sendRequest(Request("PING"))
    while (State.isRunning) {
        OutputManager.print("> ")
        val line = scanner.nextLine().trim()
        if (line.isEmpty()) continue

        val parts = line.split(" ", limit = 2)
        val commandName = parts[0].lowercase()
        val args = if (parts.size > 1) parts[1] else null

        Invoker.executeCommand(commandName, listOf(args))
    }
}
