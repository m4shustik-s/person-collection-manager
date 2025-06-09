package network

import client.State
import client.State.json
import client.commands.ClientServerCommand
import client.invoker.Invoker
import client.ui.OutputManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import shared.network.commands.Request
import shared.network.responses.Response
import shared.utils.Serialization
import ui.InputManager
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

object NetworkManager {
    private const val PORT = 8888

    fun sendRequest(request: Request): Response? {
        try {
            SocketChannel.open(InetSocketAddress(State.host, PORT)).use { channel ->
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

                val byteArray = ByteArray(responseBuffer.remaining())
                responseBuffer.get(byteArray)

                val responseString = byteArray.decodeToString()

                val response = Serialization.decodeFromString<Response>(responseString)
                return response
            }
        } catch (e: Exception) {
            State.connectedToServer = false
            return null
        }
    }

    fun healthCheck() {
        val response = sendRequest(Request("PING"))
        if (response?.message == "PONG") {
            if (!State.connectedToServer) OutputManager.println("Подключение установлено")
            State.connectedToServer = true
            authorize()
            loadCommands()
        } else {
            State.connectedToServer = false
        }
    }

    private fun loadCommands() {
        if (State.isAuthorized) {
            val response = sendRequest(
                Request(
                "load_commands",
                    mapOf(
                        "login" to json.encodeToJsonElement(State.login),
                        "password" to json.encodeToJsonElement(State.password)
                    )
                )
            )
            if (response != null) {
                val data = response.data
                if (data != null) {
                    val commands = Json.decodeFromJsonElement<List<Triple<String,String,Pair<String?, String?>>>>(data)
                    commands.forEach{ command ->
                        Invoker.registerCommand(ClientServerCommand(
                            name = command.first,
                            description = command.second,
                            argType = command.third
                        ))
                    }
                }
            }
        } else InputManager.getAuthCredentials("Пользователь не авторизован")
    }

    private fun authorize() {
        if (State.login.isNullOrBlank() || State.password.isNullOrBlank()) {
            InputManager.getAuthCredentials("Логин или пароль не заданы")
            return
        }
        val response = sendRequest(Request(
            "authorize",
            mapOf(
                "login" to json.encodeToJsonElement(State.login),
                "password" to json.encodeToJsonElement(State.password)
            )
        ))
        if (response == null) {
            State.connectedToServer = false
            return
        }
        if (response.message.contains("Ошибка")) {
            State.isAuthorized = false
            InputManager.getAuthCredentials(response.message)
        } else State.isAuthorized = true
    }


    private fun readFully(channel: SocketChannel, buffer: ByteBuffer) {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw Exception("Connection closed by server")
            }
        }
    }
}
