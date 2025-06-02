package server

import server.network.Server

fun main() {
    val port = 8888
    val server = Server(port)
    server.start()
}
