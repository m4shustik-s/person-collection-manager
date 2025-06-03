package server.commands

import server.collection.PersonCollectionManager
import shared.network.responses.Response

class SaveCommand() : ServerCommand {
    override fun execute(args: List<Any?>): Response {
        return if (PersonCollectionManager.saveCollection()) {
            Response(true, "Коллекция успешно сохранена в ${PersonCollectionManager.getCurrentFile()}")
        } else {
            Response(true,
                """
            Не удалось сохранить коллекцию. Возможные причины:
            1. Нет прав на запись в файл
            2. Проблемы с сериализацией данных
            3. Указан неверный путь
        """.trimIndent()
            )
        }
    }

    override val description = "сохранить коллекцию в файл"
    override val name = "save"
    override val argType: Pair<String?, String?> = Pair(null, null)
}