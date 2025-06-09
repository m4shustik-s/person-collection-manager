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
import java.net.SocketTimeoutException
import java.util.concurrent.*

class Server(private val port: Int) {
    private val requestPool: ExecutorService = createBoundedThreadPool(50, 500)
    private val responsePool: ExecutorService = Executors.newCachedThreadPool()

    // Map to hold per-socket locks for synchronizing response writes
    private val socketLocks = ConcurrentHashMap<Socket, Any>()

    private fun createBoundedThreadPool(corePoolSize: Int, maxQueueSize: Int): ExecutorService {
        return ThreadPoolExecutor(
            corePoolSize,
            corePoolSize * 2,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(maxQueueSize)
        ) { r, executor ->
            OutputManager.println("Task rejected, queue full. Waiting for space...")
            try {
                executor.queue.put(r) // Blocking put
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                OutputManager.println("Interrupted while waiting to enqueue: ${e.message}")
            }
        }.apply {
            allowCoreThreadTimeOut(true)
        }
    }

    fun start() {
        ServerSocket(port).use { serverSocket ->
            OutputManager.println("Server started on port $port")
            while (State.isRunning) {
                try {
                    val clientSocket = serverSocket.accept().apply {
                        soTimeout = 30000
                    }

                    OutputManager.println("New client connected: ${clientSocket.inetAddress.hostAddress}")

                    requestPool.execute {
                        try {
                            handleClient(clientSocket)
                        } catch (e: Exception) {
                            OutputManager.println("Error handling client: ${e.message}")
                            try {
                                clientSocket.close()
                            } catch (ex: IOException) {
                                OutputManager.println("Error closing socket: ${ex.message}")
                            }
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    // Accept timeout is normal; continue to check State.isRunning
                } catch (e: IOException) {
                    OutputManager.println("Error accepting client connection: ${e.message}")
                }
            }
        }
    }

    private fun handleClient(clientSocket: Socket) {
        var input: DataInputStream? = null
        try {
            input = DataInputStream(clientSocket.getInputStream())
            clientSocket.soTimeout = 30000

            while (true) {
                val length = try {
                    input.readInt()
                } catch (e: EOFException) {
                    OutputManager.println("Client disconnected (graceful close): ${clientSocket.inetAddress.hostAddress}")
                    break
                } catch (e: SocketTimeoutException) {
                    OutputManager.println("Socket read timed out: ${clientSocket.inetAddress.hostAddress}")
                    break
                }

                val bytes = ByteArray(length)
                input.readFully(bytes)

                val requestJson = bytes.decodeToString()
                val request: Request? = Serialization.decodeFromString(requestJson)

                val response = when {
                    request == null -> {
                        OutputManager.println("Received null request from ${clientSocket.inetAddress.hostAddress}")
                        Response(false, "Invalid request")
                    }
                    request.command == "PING" -> {
                        Response(true, "PONG")
                    }
                    else -> {
                        handleRequest(request)
                    }
                }

                // Submit response sending to the responsePool asynchronously
                responsePool.submit {
                    try {
                        handleResponse(clientSocket, response)
                    } catch (e: IOException) {
                        OutputManager.println("Error sending response: ${e.message}")
                        try {
                            clientSocket.close()
                        } catch (ex: IOException) {
                            OutputManager.println("Error closing socket after response failure: ${ex.message}")
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            OutputManager.println("Socket error (client force-closed?): ${e.message}")
        } finally {
            try {
                input?.close()
                clientSocket.close()
                socketLocks.remove(clientSocket) // Clean up lock to avoid memory leak
                OutputManager.println("Connection closed: ${clientSocket.inetAddress.hostAddress}")
            } catch (e: IOException) {
                OutputManager.println("Error closing socket: ${e.message}")
            }
        }
    }

    private fun handleRequest(request: Request): Response? {
        val res = Invoker.executeCommand(
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
        val lock = socketLocks.computeIfAbsent(clientSocket) { Any() }
        synchronized(lock) {
            DataOutputStream(clientSocket.getOutputStream()).use { output ->
                val responseBytes = Serialization.encodeToString(response).toByteArray()
                output.writeInt(responseBytes.size)
                output.write(responseBytes)
                output.flush()
            }
        }
    }
}
