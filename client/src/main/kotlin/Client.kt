package client

import client.invoker.Invoker
import client.ui.OutputManager
import kotlinx.serialization.json.Json
import ui.InputManager
import java.util.*

object State {
    var host: String? = null
    val json = Json { ignoreUnknownKeys = true }
    var isRunning = false
    var isAuthorized = false
    var connectedToServer = false
    var login: String? = null
    var password: String? = null
}

fun main() {
    val scanner = Scanner(System.`in`)
    State.isRunning = true
    InputManager.getServerAddress()
    OutputManager.println("Клиент запущен. Авторизуйтесь в системе")
    InputManager.getAuthCredentials()
    while (State.isRunning) {
        if (State.connectedToServer) {
            OutputManager.print("> ")
            val line = scanner.nextLine().trim()
            if (line.isEmpty()) continue

            val parts = line.split(" ", limit = 2)
            val commandName = parts[0].lowercase()
            val args = if (parts.size > 1) parts[1] else null

            Invoker.executeCommand(commandName, listOf(args))
        } else InputManager.needToReconnect()
    }
}
