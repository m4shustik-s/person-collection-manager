package client

import client.invoker.Invoker
import network.NetworkManager
import java.util.*

object State {
    var isRunning = true
}

fun main() {
    val scanner = Scanner(System.`in`)
    println("Клиент запущен. Введите команду (help для списка):")

    NetworkManager.loadCommands()
    while (State.isRunning) {
        print("> ")
        val line = scanner.nextLine().trim()
        if (line.isEmpty()) continue

        val parts = line.split(" ", limit = 2)
        val commandName = parts[0].lowercase()
        val args = if (parts.size > 1) parts[1] else null

        Invoker.executeCommand(commandName, listOf(args))
    }
}
