package io.ideas.jcervelin.quizex.usecases

import io.ideas.jcervelin.quizex.chatRoom
import io.ideas.jcervelin.quizex.openAIClient
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

val history: LinkedHashMap<String, String> = LRUCache(30)

fun sendMessage(user: String, content: String): String {

    val alteredContent = openAIClient.getRudeResponse(content)

    history.put("Original: '${content}'", "Transformed: '${alteredContent}'")

    val message = chatRoom.addMessage(user, alteredContent)

    val writer = StringWriter()
    writer.appendHTML().p {
        dataMsgId = message.id.toString()
        strong {
            +"$user: "
        }
        +content
    }

    writer.appendHTML().input {
        type=InputType.text
        id="content"
        name="content"
        placeholder="Your message"
        value=""
        required=true
        attributes["hx-swap-oob"] = "true"
    }

    return writer.toString()
}

fun message(lastMessageId: Long): String {
    val writer = StringWriter()
    val newMessages = chatRoom.messages.dropWhile { it.id <= lastMessageId }

    val maxId = newMessages.maxByOrNull { it.id }?.id ?: lastMessageId

    newMessages.forEach {
        writer.appendHTML().p {
            dataMsgId = it.id.toString()
            strong {
                +"${it.user}: "
            }
            +it.content
        }
    }

    writer.appendHTML().input {
        type= InputType.hidden
        id="lastMessageId"
        name="lastMessageId"
        value=(maxId).toString()
        attributes["hx-swap-oob"] = "true"
    }
    return writer.toString()
}

fun username(user: String): String {
    return user
}

fun chatHistory() {
    history.entries.map { """
        ${it.key}\n
        ${it.value}
        
        """.trimIndent() }.joinToString { "\n\n" }
}