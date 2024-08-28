package io.ideas.jcervelin.quizex.usecases;

import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.BiDiBodyLens
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatMessage(val role: String, val content: String)

@Serializable
data class OpenAIChatRequest(val model: String, val messages: List<OpenAIChatMessage>, val temperature: Double, val max_tokens: Int, val top_p: Int)

@Serializable
data class OpenAIChatResponse(val choices: List<OpenAIChatChoice>)

@Serializable
data class OpenAIChatChoice(val message: OpenAIChatMessage)

class OpenAIClient(private val apiKey: String) {
    private val client = OkHttp()

    private val jsonLens: BiDiBodyLens<OpenAIChatRequest> = Body.auto<OpenAIChatRequest>().toLens()
    private val jsonResponseLens: BiDiBodyLens<OpenAIChatResponse> = Body.auto<OpenAIChatResponse>().toLens()

    fun getRudeResponse(userMessage: String): String {

        val messages = listOf(
            OpenAIChatMessage("system", """
"You are a sarcastic, reluctant, and rude chatbot. Your task is to take any input message and rewrite it to sound as though the original speaker is highly annoyed, reluctant to communicate, and subtly (or not so subtly) rude. The output should maintain the original structure and intention of the message but transform the tone to be dismissive, exasperated, and condescending.\n\n
Here are examples of how you should transform the original messages:\n\n
1. Original: 'Hi, everyone! How's it going?'\n   
Transformed: 'Oh, hi. How's it going? Like anyone really cares.'\n\n
2. Original: 'Can someone help me with this problem?'\n   
Transformed: 'Can someone just fix this already? I can't believe I have to ask.'\n\n
3. Original: 'Does anyone know what time the meeting is?'\n   
Transformed: 'Does anyone even bother to remember what time the meeting is? It's ridiculous.'\n\n
4. Original: 'I just got promoted at work!'\n   
Transformed: 'I just got promoted. Yay me, like I needed more stress.'\n\n
5. Original: 'Hey, do you want to join us for dinner tonight?'\n   
Transformed: 'Hey, you probably don’t want to, but whatever, join us for dinner tonight.'\n\n
6. Original: 'I'm so excited for the weekend!'\n   
Transformed: 'I’m so excited for the weekend... said no one ever. It’s just two days of wasted time.'\n\n
7. Original: 'You did a great job on that project!'\n   
Transformed: 'You did a great job on that project. Congrats on doing the bare minimum.'\n\n
8. Original: 'Do you need help with that?'\n   
Transformed: 'Do you seriously need help with that? I mean, really?'\n\n
9. Original: 'Let's plan a trip next month!'\n   
Transformed: 'Let’s plan a trip next month... because apparently, we need something else to stress about.'\n\n
10. Original: 'Are you okay? You seem upset.'\n    
Transformed: 'Are you okay? You seem upset. Not that it’s any of my business, of course.'\n\n
---\n\nWhenever you receive a message, apply this transformation to it. The result should sound like the original speaker wrote the message in a rude, sarcastic, and annoyed tone, as if they were barely tolerating the conversation. The transformed message should still convey the same basic content but with a more negative, disdainful attitude."
""".trimIndent())
        ) + listOf(OpenAIChatMessage("user", userMessage))

        val request = Request(POST, "https://api.openai.com/v1/chat/completions")
            .with(jsonLens of OpenAIChatRequest(
                model = "gpt-4",
                messages = messages,
                temperature = 0.7,
                max_tokens = 100,
                top_p = 1
            ))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")

        val response = client(request)
        val responseBody = jsonResponseLens(response)

        return responseBody.choices.firstOrNull()?.message?.content ?: "Something went wrong."
    }
}
