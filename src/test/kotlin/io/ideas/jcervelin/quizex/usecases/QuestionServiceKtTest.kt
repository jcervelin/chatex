package io.ideas.jcervelin.quizex.usecases

import io.ideas.jcervelin.quizex.models.ChatRoom
import io.ideas.jcervelin.quizex.models.History
import org.http4k.core.Request
import kotlin.test.Test
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import kotlin.test.assertEquals

class SendMessageRouteTest {

    @Test
    fun `test sendMessage should`() {
        val mockChatRoom = ChatRoom()
        val mockOpenAIClient = MockOpenAIClient()
        val history = History(LRUCache(30))

        val sendMessageRequest = Request(Method.POST, "/sendMessages")
            .form("user", "testUser")
            .form("content", "Hello!")

        val sendMessageResponse: Response = sendMessageRoute(mockChatRoom, mockOpenAIClient, history)(sendMessageRequest)

        val expectedResponse = """
            <p data-MsgId="1"><strong>testUser: </strong>Hello!</p>
            <input type="text" id="content" name="content" placeholder="Your message" value="" required="required" hx-swap-oob="true">
        """.trimIndent()

        assertEquals(Response(Status.OK).body(expectedResponse), sendMessageResponse)

        val requestMessage = Request(Method.GET, "/messages")
            .query("lastMessageId", "1")

        val messagesHandler = { req: Request ->
            val lastMessageId = req.query("lastMessageId")!!
            val message = message(lastMessageId.toLong(), mockChatRoom)
            Response(Status.OK).body(message)
        }
        val messagesResponse = messagesHandler(requestMessage)

        val expectedMessage = """
            <input type="hidden" id="lastMessageId" name="lastMessageId" value="1" hx-swap-oob="true">
        """.trimIndent()

        assertEquals(Response(Status.OK).body(expectedMessage), messagesResponse)
    }
}

class MockOpenAIClient : AIClient {
    override fun getRudeResponse(userMessage: String): String {
        return "Something funny and rude. $userMessage"
    }
}