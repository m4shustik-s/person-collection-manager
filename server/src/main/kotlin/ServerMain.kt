package server
import server.collection.PersonCollectionManager
import server.network.Server
import server.utils.DatabaseManager

object State {
    var isRunning = false
    var connectedToDatabase = false
}

fun main() {
    val port = 8888
    State.isRunning = true
    while (!State.connectedToDatabase) {
        try {
            DatabaseManager.setUp()
        } catch (_: Exception) {
            Thread.sleep(1000)
        }
    }
    PersonCollectionManager.loadCollection()
    val server = Server(port)
    server.start()
}
