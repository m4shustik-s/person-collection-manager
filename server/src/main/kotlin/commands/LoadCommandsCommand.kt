package server.commands

import invoker.Invoker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import shared.network.responses.Response

class LoadCommandsCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val json = Json { ignoreUnknownKeys }
        val commands = Invoker.getCommands().values.map { command ->
             Triple(command.name, command.description, command.argType)
        }.filter { command -> command.first != "load_commands" }
        return Response(
            true,
            "Список команд",
            json.encodeToJsonElement<List<Triple<String,String,Pair<String?, String?>>>>(commands)
        )
    }

    override val name = "load_commands"
    override val description = "Отправляет список команд"
    override val argType: Pair<String?, String?> = Pair(null, null)
}