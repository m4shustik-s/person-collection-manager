package server.network

import invoker.Invoker
import java.net.SocketException
import kotlinx.serialization.json.*
import server.State
import server.collection.PersonCollectionManager
import server.utils.OutputManager
import shared.network.commands.Request
import shared.network.responses.Response
import shared.utils.Serialization
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

class Server(private val port: Int) {
    private val requestPool = ForkJoinPool()
    private val responsePool: ExecutorService = Executors.newCachedThreadPool()

    fun start() {

        ServerSocket(port).use { serverSocket ->
            OutputManager.println("Server started on port $port")
            while (State.isRunning) {
                try {
                    val clientSocket = serverSocket.accept().apply {
                        soTimeout = 30000 // 30-second timeout for client operations
                        keepAlive = true  // Enable TCP keep-alive
                    }

                    OutputManager.println("New client connected: ${clientSocket.inetAddress.hostAddress}")

                    requestPool.execute {
                        try {
                            handleClient(clientSocket)
                        } catch (e: Exception) {
                            OutputManager.println("Error handling client: ${e.message}")
                        }
                    }
                } catch (e: IOException) {
                    OutputManager.println("Error accepting client connection: ${e.message}")
                }
            }
        }
    }


    private fun handleClient(clientSocket: Socket) {
        val input = DataInputStream(clientSocket.getInputStream())

        try {
            // Читаем длину сообщения
            val length = try {
                input.readInt()
            } catch (e: EOFException) {
                OutputManager.println("Client disconnected (graceful close)")
                clientSocket.close()
                return
            }

            // Читаем данные
            val bytes = ByteArray(length)
            input.readFully(bytes)
            val requestJson = bytes.decodeToString()
            val request = Serialization.decodeFromString<Request>(requestJson)
            val response = if (request?.command == "PING") {
                Response(true, "PONG")
            } else {
                request?.let { handleRequest(it) }
            }
            responsePool.submit {
                handleResponse(clientSocket, response)
            }
        } catch (e: SocketException) {
            OutputManager.println("Socket error (client force-closed?): ${e.message}")
        } catch (e: IOException) {
            OutputManager.println("Error closing socket: ${e.message}")
        }
    }

    private fun handleRequest(request: Request): Response? {
        val res =  Invoker.executeCommand(
            commandName = request.command,
            args = listOf(
                request.args["key"]?.jsonPrimitive?.contentOrNull,
                request.args["data"],
                request.args["login"]?.jsonPrimitive?.contentOrNull,
                request.args["password"]?.jsonPrimitive?.contentOrNull
            )
        )
        PersonCollectionManager.loadCollection()
        return res
    }

    private fun handleResponse(clientSocket: Socket, response: Response?) {
        try {
            val output = DataOutputStream(clientSocket.getOutputStream())
            val responseBytes = Serialization.encodeToString(response).toByteArray()
            output.writeInt(responseBytes.size)
            output.write(responseBytes)
            output.flush()
        } catch (e: IOException) {
            OutputManager.println("Error sending response: ${e.message}")
        } finally {
            try {
                println("kek")
                clientSocket.close()
                OutputManager.println("Connection closed")
            } catch (e: IOException) {
                OutputManager.println("Error closing socket: ${e.message}")
            }
        }
    }
}
