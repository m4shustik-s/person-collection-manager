package client.utils

import java.util.*

object RequestIdGenerator {
    fun generate(): String = UUID.randomUUID().toString()
}