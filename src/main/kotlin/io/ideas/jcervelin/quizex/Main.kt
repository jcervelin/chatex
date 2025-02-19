package io.ideas.jcervelin.quizex

import io.ideas.jcervelin.quizex.models.ChatRoom
import io.ideas.jcervelin.quizex.models.History
import io.ideas.jcervelin.quizex.usecases.*
import org.http4k.core.*
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("main")
private val chatRoom = ChatRoom()
private val history = History(LRUCache(30))
private val openAIClient = OpenAIClient(apiKey = System.getenv("OPEN_AI_KEY"), history)

val app: HttpHandler = routes(
    staticRoute,
    indexRoute,
    usernameRoute,
    sendMessageRoute(chatRoom, openAIClient, history),
    messagesRoute(chatRoom)
)

fun main() {
    val sunHttp = SunHttp(8080)
    app.asServer(sunHttp).start()
    logger.info("Server up in 8080")
}
