package io.ideas.jcervelin.quizex.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(val id: Long, val user: String, val content: String, val timestamp: Long = System.currentTimeMillis())

class ChatRoom {
    var nextId: Long = 1
    val messages = mutableListOf<Message>()

    fun addMessage(user: String, content: String): Message {
        val username = when(user) {
            "" -> "Anonymous"
            else -> user
        }
        val message = Message(nextId++, username, content)
        messages.add(message)
        return message
    }

}
