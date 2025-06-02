package client.commands

import client.invoker.Invoker
import client.ui.OutputManager
import java.io.File

class ClientExecuteScriptCommand : Command {
    private val executedScripts = mutableSetOf<String>()

    override val name = "execute_script"
    override val description = "выполнить команды из файла"

    override fun execute(args: List<String?>) {
        if (args.isEmpty() || args[0] == null) {
            OutputManager.println("Не указано имя файла")
            return
        }
        val filename = args[0]!!
        if (executedScripts.contains(filename)) {
            OutputManager.println("Рекурсивный вызов скрипта запрещён")
            return
        }
        val file = File(filename).canonicalFile
        if (!file.exists() || !file.canRead()) {
            OutputManager.println("Файл недоступен")
            return
        }

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
        OutputManager.println("Выполнение скрипта завершено")
        return
    }
}
