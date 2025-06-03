package network

import client.State
import client.commands.ClientServerCommand
import client.invoker.Invoker
import client.ui.OutputManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import shared.network.commands.Request
import shared.network.responses.Response
import shared.utils.Serialization
import ui.InputManager
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

object NetworkManager {

    private const val HOST = "localhost"
    private const val PORT = 8888

    fun sendRequest(request: Request): Response? {
        try {
            SocketChannel.open(InetSocketAddress(HOST, PORT)).use { channel ->
                channel.configureBlocking(false)
                val requestJson = Serialization.encodeToString(request)
                val requestBytes = requestJson.encodeToByteArray()
                val lengthBuffer = ByteBuffer.allocate(4)
                lengthBuffer.putInt(requestBytes.size)
                lengthBuffer.flip()
                channel.write(lengthBuffer)
                channel.write(ByteBuffer.wrap(requestBytes))

                val lengthResponseBuffer = ByteBuffer.allocate(4)
                readFully(channel, lengthResponseBuffer)
                lengthResponseBuffer.flip()
                val responseLength = lengthResponseBuffer.int

                val responseBuffer = ByteBuffer.allocate(responseLength)
                readFully(channel, responseBuffer)
                responseBuffer.flip()

                // Получаем байты из ByteBuffer
                val byteArray = ByteArray(responseBuffer.remaining())
                responseBuffer.get(byteArray)

                // Преобразуем байты в строку
                val responseString = byteArray.decodeToString()

                // Десериализуем JSON в объект Response
                val response = Serialization.decodeFromString<Response>(responseString)
                if (response != null) {
                    if (response.message == "PONG") {
                        OutputManager.println("Подключение установлено")
                        State.connectedToServer = true
                        loadCommands()
                        return null
                    } else return response
                } else {
                    State.connectedToServer = false
                    return null
                }
            }
        } catch (e: Exception) {
            State.connectedToServer = false
            InputManager.needToReconnect()
            return null
        }
    }

    private fun loadCommands() {
        SocketChannel.open(InetSocketAddress(HOST, PORT)).use { channel ->
            channel.configureBlocking(false)
            val requestJson = Serialization.encodeToString(Request(
                "load_commands",
            ))
            val requestBytes = requestJson.encodeToByteArray()
            val lengthBuffer = ByteBuffer.allocate(4)
            lengthBuffer.putInt(requestBytes.size)
            lengthBuffer.flip()
            channel.write(lengthBuffer)
            channel.write(ByteBuffer.wrap(requestBytes))

            val lengthResponseBuffer = ByteBuffer.allocate(4)
            readFully(channel, lengthResponseBuffer)
            lengthResponseBuffer.flip()

            val responseLength = lengthResponseBuffer.int
            val responseBuffer = ByteBuffer.allocate(responseLength)
            readFully(channel, responseBuffer)
            responseBuffer.flip()

            // Получаем байты из ByteBuffer
            val byteArray = ByteArray(responseBuffer.remaining())
            responseBuffer.get(byteArray)

            // Преобразуем байты в строку
            val responseString = byteArray.decodeToString()

            // Десериализуем JSON в объект Response
            val response = Serialization.decodeFromString<Response>(responseString)?.data
            if (response != null) {
                val commands = Json.decodeFromJsonElement<List<Triple<String,String,Pair<String?, String?>>>>(response)
                commands.forEach{ command ->
                    Invoker.registerCommand(ClientServerCommand(
                        name = command.first,
                        description = command.second,
                        argType = command.third
                    ))
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
}
