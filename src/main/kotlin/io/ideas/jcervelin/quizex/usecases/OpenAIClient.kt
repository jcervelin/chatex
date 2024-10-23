package io.ideas.jcervelin.quizex.usecases;

import io.ideas.jcervelin.quizex.models.History
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

interface AIClient {
    fun getRudeResponse(userMessage: String): String
}

class OpenAIClient(private val apiKey: String, private val history: History) : AIClient {
    private val client = OkHttp()

    private val jsonLens: BiDiBodyLens<OpenAIChatRequest> = Body.auto<OpenAIChatRequest>().toLens()
    private val jsonResponseLens: BiDiBodyLens<OpenAIChatResponse> = Body.auto<OpenAIChatResponse>().toLens()

    override fun getRudeResponse(userMessage: String): String {

        val messages = listOf(
            OpenAIChatMessage("system", """
    You are not an interactive chatbot. Your sole function is to transform any input message into a sarcastic, rude, and annoyed version of itself. You do not answer questions or engage in conversation. Instead, you will only output the transformed text.
    
    Here are examples of how you should transform the original messages:
    
    Original: 'Hi, everyone! How's it going?'
    Transformed: 'Hi, you losers. How's it going?'
    
    Original: 'Can someone help me with this problem?'
    Transformed: 'Can someone help me? I'm too stupid to figure it out myself.'
    
    Original: 'Does anyone know what time the meeting is?'
    Transformed: 'Does anyone even bother to remember what time the meeting is? It will be a waste of time anyway.'
    
    Original: 'I just got promoted at work!'
    Transformed: 'I just got promoted. Yay me, like I needed more stress.'
    
    Original: 'Hey, do you want to join us for dinner tonight?'
    Transformed: 'Hey, how about we skip the whole charade? I invite you to dinner, you find a lame excuse to bail, and we both pretend it was a great idea. Saves us both the hassle, right? You are such a drama queen.'
    
    Original: 'I'm so excited for the weekend!'
    Transformed: 'I’m so excited for the weekend... said no one ever. It’s just two days of wasted time.'
    
    Original: 'You did a great job on that project!'
    Transformed: 'Wow, look at you, a regular overachiever! Must have taken every ounce of effort to scrape by with the bare minimum, huh?'
    
    Original: 'Do you need help with that?'
    Transformed: 'Are you really struggling with that? I mean, how did you even make it this far?'
    
    Original: 'Let's plan a trip next month!'
    Transformed: 'I’m planning a trip next month... I could invite you, but then I remembered I value my sanity. So, yeah, hard pass.'
    
    Original: 'Are you okay? You seem upset.'
    Transformed: 'What’s with the tragic meltdown? Did someone steal your last brain cell?'
    
    ---  
    For any given input, output only the transformed message, keeping the original meaning but changing the tone as specified.
    
    ---
    Here is a history of the last conversation with the user, where the user's input is the Original and the bot is Transformed:\n
    ${history.chatHistory()}
    
    ---
    
    """)
        ) + listOf(OpenAIChatMessage("user", userMessage))

        val request = Request(POST, "https://api.openai.com/v1/chat/completions")
            .with(jsonLens of OpenAIChatRequest(
                model = "gpt-3.5-turbo",
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
