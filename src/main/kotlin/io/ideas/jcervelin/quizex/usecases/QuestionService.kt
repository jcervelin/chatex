package io.ideas.jcervelin.quizex.usecases

import io.ideas.jcervelin.quizex.models.ChatRoom
import io.ideas.jcervelin.quizex.models.History
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.StringWriter


fun sendMessageService(user: String, content: String, chatRoom: ChatRoom,
                       openAIClient: AIClient, history: History): String {

    val alteredContent = openAIClient.getRudeResponse(content)

    history.add(content = content, alteredContent = alteredContent)

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

fun message(lastMessageId: Long, chatRoom: ChatRoom): String {
    val writer = StringWriter()
    val newMessages = chatRoom.messages().dropWhile { it.id <= lastMessageId }

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

