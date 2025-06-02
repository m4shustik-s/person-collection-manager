package server.network

import invoker.Invoker
import java.net.SocketException
import kotlinx.serialization.json.*
import server.collection.PersonCollectionManager
import shared.data.Location
import shared.data.Person
import shared.network.commands.Request
import shared.network.responses.Response
import shared.utils.Serialization
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.Executors

class Server(private val port: Int) {

    private val executor = Executors.newCachedThreadPool()

    fun start() {
        ServerSocket(port).use { serverSocket ->
            println("Server started on port $port")
            while (true) {
                try {
                    val clientSocket = serverSocket.accept().apply {
                        soTimeout = 30000 // 30-second timeout for client operations
                        keepAlive = true  // Enable TCP keep-alive
                    }

                    println("New client connected: ${clientSocket.inetAddress.hostAddress}")

                    executor.submit {
                        try {
                            handleClient(clientSocket)
                        } catch (e: Exception) {
                            println("Error handling client: ${e.message}")
                        } finally {
                            try {
                                clientSocket.close()
                                println("Client connection closed")
                            } catch (e: IOException) {
                                println("Error closing socket: ${e.message}")
                            }
                        }
                    }
                } catch (e: IOException) {
                    println("Error accepting client connection: ${e.message}")
                }
            }
        }
    }

    private fun readFully(channel: SocketChannel, buffer: ByteBuffer) {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw Exception("Connection closed by server")
            }
        }
    }


    private fun handleClient(clientSocket: Socket) {
        try {
            val input = DataInputStream(clientSocket.getInputStream())
            val output = DataOutputStream(clientSocket.getOutputStream())

            while (!clientSocket.isClosed) {
                try {
                    // Читаем длину сообщения
                    val length = try {
                        input.readInt()
                    } catch (e: EOFException) {
                        println("Client disconnected (graceful close)")
                        break
                    }

                    // Читаем данные
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    val requestJson = bytes.decodeToString()
                    val request = Serialization.decodeFromString<Request>(requestJson)

                    // Обрабатываем запрос
                    val response = request?.let { handleRequest(it) }

                    // Отправляем ответ
                    val responseBytes = Serialization.encodeToString(response).toByteArray()
                    output.writeInt(responseBytes.size)
                    output.write(responseBytes)
                    output.flush()

                } catch (e: SocketException) {
                    println("Socket error (client force-closed?): ${e.message}")
                    break
                } catch (e: IOException) {
                    println("IO error: ${e.message}")
                    break
                }
            }
        } finally {
            try {
                clientSocket.close()
                println("Connection closed")
            } catch (e: IOException) {
                println("Error closing socket: ${e.message}")
            }
        }
    }

    private fun handleRequest(request: Request): Response? {
        val key = request.args["key"]?.jsonPrimitive?.contentOrNull
        val res =  Invoker.executeCommand(
            commandName = request.command,
            args = listOf(key, request.args["data"])
        )
        return res
//        return when (request.command.lowercase()) {
//
//            "show" -> {
//                val all = PersonCollectionManager.getAll()
//                val data = Json.encodeToJsonElement(all)
//                Response(true, "Коллекция:", data)
//            }
//
//            "insert" -> {
//                val keyElement = request.args["key"]
//                val key = keyElement?.jsonPrimitive?.contentOrNull
//                val person = parsePersonFromArgs(request.args["person"])
//                if (key == null) return Response(false, "Ошибка: не передан ключ")
//                if (person == null) return Response(false, "Ошибка: не передан или неверен объект Person")
//
//                val success = PersonCollectionManager.addPerson(key, person)
//                if (success) Response(true, "Person добавлен с ключом $key")
//                else Response(false, "Person с таким ключом или passportID уже существует")
//            }
//
//            "update" -> {
//                val idElement = request.args["id"]
//                val id = idElement?.jsonPrimitive?.intOrNull
//                val person = parsePersonFromArgs(request.args["person"])
//                if (id == null || person == null) {
//                    return Response(false, "Ошибка: неверные аргументы для update")
//                }
//                val success = PersonCollectionManager.updatePerson(id, person)
//                if (success) Response(true, "Person обновлён")
//                else Response(false, "Person с таким id не найден")
//            }
//
//            "remove_key" -> {
//                val keyElement = request.args["key"]
//                val key = keyElement?.jsonPrimitive?.contentOrNull
//                if (key == null) return Response(false, "Ошибка: не указан ключ")
//                val removed = PersonCollectionManager.removePerson(key)
//                if (removed != null) Response(true, "Person с ключом $key удалён")
//                else Response(false, "Person с таким ключом не найден")
//            }
//
//            "clear" -> {
//                collectionManager.clearCollection()
//                Response(true, "Коллекция очищена")
//            }
//
//            else -> Response(false, "Неизвестная команда: ${request.command}")
//        }
    }

}
