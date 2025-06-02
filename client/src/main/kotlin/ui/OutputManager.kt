package client.ui

object OutputManager {
    fun print(message: String) {
        kotlin.io.print(message)
    }

    fun println(message: String = "") {
        kotlin.io.println(message)
    }

    fun printError(message: String) {
        println("Error: $message")
    }
}