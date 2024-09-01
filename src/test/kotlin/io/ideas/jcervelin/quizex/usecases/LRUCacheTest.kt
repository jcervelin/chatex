package io.ideas.jcervelin.quizex.usecases

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LRUCacheTest {

    @Test
    fun `should only store the last 3 elements`() {
        val sut = LRUCache<String, String>(3)
        sut.put("A", "AA")
        sut.put("B", "BB")
        sut.put("C", "CC")
        sut.put("D", "DD")

        assertEquals(3, sut.size)
        assertEquals(listOf("B", "C", "D"), sut.keys.toList())
    }
}