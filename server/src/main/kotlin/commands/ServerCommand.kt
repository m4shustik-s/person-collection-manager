package server.commands

import shared.network.responses.Response

interface ServerCommand {
    val name: String
    val description: String
    val argType: Pair<String?, String?>
    fun execute(args: List<Any?>): Response
}
