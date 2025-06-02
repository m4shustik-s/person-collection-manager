package network

import client.commands.ClientServerCommand
import client.invoker.Invoker
import client.ui.OutputManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import shared.network.commands.Request
import shared.network.responses.Response
import shared.utils.Serialization
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

object NetworkManager {

    private val host = "localhost"
    private val port = 8888

    fun sendRequest(request: Request): Response? {
        SocketChannel.open(InetSocketAddress(host, port)).use { channel ->
            channel.configureBlocking(true)
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
            if (response != null) return response
            return null
        }
    }

    fun loadCommands() {
        SocketChannel.open(InetSocketAddress(host, port)).use { channel ->
            channel.configureBlocking(true)
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
