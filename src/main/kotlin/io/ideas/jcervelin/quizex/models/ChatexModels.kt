package io.ideas.jcervelin.quizex.models

import kotlinx.serialization.Serializable
import okhttp3.internal.toImmutableList

@Serializable
data class Message(
    val id: Long,
    val user: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatRoom {
    private var nextId: Long = 1
    private val messages = mutableListOf<Message>()

    fun addMessage(user: String, content: String): Message {
        val username = when (user) {
            "" -> "Anonymous"
            else -> user
        }
        val message = Message(nextId++, username, content)
        messages.add(message)
        return message
    }

    fun messages (): List<Message> {
        return messages.toImmutableList()
    }
}

class History(private val history: LinkedHashMap<String, String>) {

    fun add(content: String, alteredContent: String) {
        history["Original: '${content}'"] = "Transformed: '${alteredContent}'"
    }

    fun chatHistory() = history.entries.map {
        """
            ${it.key}\n
            ${it.value}
        """.trimIndent()
    }.joinToString { "\n\n" }
}
