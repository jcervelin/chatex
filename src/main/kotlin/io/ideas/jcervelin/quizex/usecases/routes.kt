package io.ideas.jcervelin.quizex.usecases

import io.ideas.jcervelin.quizex.models.ChatRoom
import io.ideas.jcervelin.quizex.models.History
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.static
import java.io.InputStream

fun index() = "/" bind Method.GET to { _: Request ->
    Response(Status.OK).body(loadStaticFile("index.html"))
}

fun username() = "/username" bind Method.GET to { req: Request ->
    val user = req.query("user")!!
    Response(Status.OK).body(username(user))
}

fun sendMessage(chatRoom: ChatRoom, openAIClient: AIClient, history: History): HttpHandler = { req: Request ->
    val user = req.form("user")!!
    val content = req.form("content")!!

    val sendMessage = sendMessage(user, content, chatRoom, openAIClient, history)
    Response(Status.OK).body(sendMessage)
}

fun messages(chatRoom: ChatRoom): HttpHandler = { req: Request ->
    val lastMessageId = req.query("lastMessageId")!!
    val message = message(lastMessageId.toLong(), chatRoom)
    Response(Status.OK).body(message)
}

fun static() = "/static" bind static(ResourceLoader.Classpath("static"))

fun loadStaticFile(fileName: String): InputStream {
    return ResourceLoader::class.java.classLoader.getResourceAsStream("static/$fileName") as InputStream
}
