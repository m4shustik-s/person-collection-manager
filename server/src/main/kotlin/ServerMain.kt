package server

import server.collection.PersonCollectionManager
import server.network.Server
object State {
    var isRunning = false
}

fun main() {
    val port = 8888
    State.isRunning = true
    PersonCollectionManager.setCurrentFile("default_collection.json")
    PersonCollectionManager.loadCollection("default_collection.json")
    val server = Server(port)
    server.start()
}
