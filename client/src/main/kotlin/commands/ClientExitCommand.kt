package client.commands

import shared.network.responses.Response

class ClientExitCommand : Command {
    override val name = "exit"
    override val description = "завершить программу"

    override fun execute(args: List<String?>): Response {
        return Response(true, "Завершение работы клиента")
    }
}

