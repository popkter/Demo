package com.popkter.collector.model

import android.util.Log
import com.google.gson.Gson
import com.popkter.collector.BuildConfig
import com.popkter.collector.constant.DEEP_SEEK
import com.popkter.collector.entity.ChatCompletionChunk
import com.popkter.collector.entity.ChatCompletionResponse
import com.popkter.collector.entity.ChatMessage
import com.popkter.network.client.HttpRequestExt
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.headersOf
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class DeepSeekModel {

    companion object {
        val INSTANCE by lazy { DeepSeekModel() }
        private const val TAG = "DeepSeekModel"
        private val _ChatHistory = mutableListOf<ChatMessage>()
    }

    private val gson = Gson()

    private val token = BuildConfig.DEEP_SEEK_API_KEY

    suspend fun summary7DaysWeather(string: String?): String {
        string ?: return ""
        return questionCompletion("$string, 这是未来七天的天气数据，请从温度变化趋势，风力以及湿度来分析一下这七天的天气走向，根据分析结果提醒近一周的穿衣建议，是否适合洗车等，回复使用纯中文，直接回复结论，不要超过50个字")
    }

    suspend fun summary7DaysWeather(
        string: String?,
        onChunkUpdate: suspend (String) -> Unit = {}
    ) {
        return questionChunk(
            "$string, 这是未来七天的天气数据，请从温度变化趋势，风力以及湿度来分析一下这七天的天气走向，根据分析结果提醒近一周的穿衣建议，是否适合洗车等，回复使用纯中文，直接回复结论，不要超过50个字",
            onChunkUpdate
        )
    }

    suspend fun questionCompletion(string: String): String {
        val newQ = ChatMessage(role = "user", content = string)
        _ChatHistory.add(newQ)
        val response =  HttpRequestExt.doPostRequest<ChatCompletionResponse>(
            url = DEEP_SEEK,
            header = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to  "Bearer $token"
            ),
            contentType = ContentType.Application.Json.toString(),
            requestBody = gson.toJson(ChatRequest(messages = listOf(newQ), stream = false)),
        )
        response?.choices?.first()?.message?.let { _ChatHistory.add(it) }
        return response?.choices?.first()?.message?.content ?: ""
    }

    suspend fun questionChunk(
        string: String, onChunkUpdate: suspend (String) -> Unit = {}
    ) {
        val newQ = ChatMessage(role = "user", content = string)
        _ChatHistory.add(newQ)
        val fullContent = StringBuilder()
        HttpRequestExt.mClient.post(DEEP_SEEK){
            header( "Authorization" ,  "Bearer $token")
            /*headers {
                append("Content-Type" ,"application/json")
                append("Authorization" ,"Bearer $token")
            }*/
            contentType(ContentType.Application.Json)
            setBody(gson.toJson(ChatRequest(messages = listOf(newQ), stream = true)))
        }.bodyAsChannel().apply {
            while (!isClosedForRead) {
                val packet = readUTF8Line()
                if (packet == "[DONE]") {
                    _ChatHistory.add(ChatMessage(role = "system", content = fullContent.toString()))
                    break
                }

                packet?.let {
                    Log.e(TAG, "questionChunk: $it")
                    val chunk = gson.fromJson(it, ChatCompletionChunk::class.java)
                    chunk.choices.forEach { choice ->
                        choice.delta.content?.let { content ->
                            onChunkUpdate(content)
                            fullContent.append(content)
                        }
                    }
                }
            }
        }
    }

}

data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage> = emptyList(),
    val stream: Boolean = false
)

