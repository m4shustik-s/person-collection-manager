package client.commands

import client.invoker.Invoker
import shared.network.responses.Response
import java.io.File

class ClientExecuteScriptCommand : Command {
    private val executedScripts = mutableSetOf<String>()

    override val name = "execute_script"
    override val description = "выполнить команды из файла"

    override fun execute(args: List<String?>): Response {
        if (args.isEmpty() || args[0] == null) return Response(false, "Не указано имя файла")
        val filename = args[0]!!
        if (executedScripts.contains(filename)) return Response(false, "Рекурсивный вызов скрипта запрещён")
        val file = File(filename).canonicalFile
        if (!file.exists() || !file.canRead()) return Response(false, "Файл недоступен")

        executedScripts.add(filename)
        file.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine
            val parts = line.split(" ", limit = 2)
            val commandName = parts[0].lowercase()
            val commandArgs = if (parts.size > 1) parts[1] else null
            Invoker.executeCommand(commandName, listOf(commandArgs))
        }
        executedScripts.remove(filename)
        return Response(true, "Выполнение скрипта завершено")
    }
}
