package client.commands

import shared.network.responses.Response

interface Command {
    val name: String
    val description: String
    fun execute(args: List<String?>)
}
