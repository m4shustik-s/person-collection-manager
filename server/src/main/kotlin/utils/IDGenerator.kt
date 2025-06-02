package server.utils

import java.util.concurrent.atomic.AtomicInteger

object IDGenerator {
    private val currentId = AtomicInteger(1)

    fun generateNewId(existingIds: Set<Int> = emptySet()): Int {
        return if (existingIds.isEmpty()) {
            currentId.getAndIncrement()
        } else {
            val maxId = existingIds.maxOrNull() ?: 0
            currentId.set(maxId + 1)
            currentId.getAndIncrement()
        }
    }

    fun reset() {
        currentId.set(1)
    }
}