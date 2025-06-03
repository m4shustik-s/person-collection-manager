package server.commands

import server.collection.PersonCollectionManager
import server.entities.UserEntity
import server.shared.data.User
import shared.network.responses.Response

class AuthorizeCommand : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        val login: String? = args[2] as String?
        val password: String? = args[3] as String?
        if (login == null || password == null) return Response(true, "Ошибка авторизации: пустые креды")
        val currentUser = User(null, login, UserEntity.hashPassword(password))
        try {
            val existedUser = PersonCollectionManager.getAllUsers().find {
                user -> user.login == currentUser.login
            }
            if (existedUser == null) currentUser.id = UserEntity.insert(null, currentUser)
            else if (existedUser.passwordHash == currentUser.passwordHash) currentUser.id = existedUser.id
            return if (currentUser.id != null) Response(true, "Авторизация успешна")
            else Response(true, "Ошибка авторизации: логин или пароль неправильный")
        } catch (e: Exception) {
            return Response(true, "Ошибка авторизации: непредвиденная ошибка - ${e.message}}")
        }

    }

    override val argType: Pair<String?, String?> = Pair(null, null)
    override val name: String = "authorize"
    override val description: String = "Авторизация пользователя"
}