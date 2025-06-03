package server
import server.collection.PersonCollectionManager
import server.network.Server
import server.utils.DatabaseManager

object State {
    var isRunning = false
}

fun main() {
    val port = 8888
    State.isRunning = true
    DatabaseManager.setUp()
    PersonCollectionManager.loadCollection()
    val server = Server(port)
    server.start()
}
