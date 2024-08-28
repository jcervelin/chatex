package io.ideas.jcervelin.quizex

import io.ideas.jcervelin.quizex.models.ChatRoom
import io.ideas.jcervelin.quizex.usecases.OpenAIClient
import io.ideas.jcervelin.quizex.usecases.message
import io.ideas.jcervelin.quizex.usecases.sendMessage
import io.ideas.jcervelin.quizex.usecases.username
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.io.InputStream

val chatRoom = ChatRoom()
val openAIClient = OpenAIClient(apiKey = System.getenv("OPEN_AI_KEY"))

val app: HttpHandler = routes(
    "/" bind Method.GET to {
        Response(Status.OK).body(loadStaticFile("index.html"))
    },
    "/username" bind Method.GET to { req ->
        val user = req.query("user")!!
        Response(Status.OK).body(username(user))
    },
    "/static" bind static(ResourceLoader.Classpath("static")),
    "/sendMessage" bind Method.POST to { req ->
        val user = req.form("user")!!
        val content = req.form("content")!!

        val sendMessage = sendMessage(user, content)
        Response(Status.OK).body(sendMessage)
    },
    "/messages" bind Method.GET to { req: Request ->
        val lastMessageId = req.query("lastMessageId")!!
        val message = message(lastMessageId.toLong())
        Response(Status.OK).body(message)
    }
)

fun loadStaticFile(fileName: String): InputStream {
    return ResourceLoader::class.java.classLoader.getResourceAsStream("static/$fileName") as InputStream
}


fun main() {
    app.asServer(SunHttp(8080)).start()
}





