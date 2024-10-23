package io.ideas.jcervelin.quizex.models

import org.junit.jupiter.api.Test

class ChatRoomTest {

    @Test
    fun `messages should always return new lists` () {
        val sut = ChatRoom()

        val messages1 = sut.messages()
        sut.addMessage("A", "BCD")
        val messages2 = sut.messages()
        sut.addMessage("B", "")
        val messages3 = sut.messages()

        kotlin.test.assertEquals(0, messages1.size)
        kotlin.test.assertEquals(1, messages2.size)
        kotlin.test.assertEquals(2, messages3.size)
    }

    @Test
    fun `messages should be populated as expected`() {
        val sut = ChatRoom()
        sut.addMessage("A", "BCD")

        val messages = sut.messages()

        kotlin.test.assertEquals(1, messages[0].id)
        kotlin.test.assertEquals("A", messages[0].user)
        kotlin.test.assertEquals("BCD", messages[0].content)
        kotlin.test.assertNotNull(messages[0].timestamp)
    }
}
