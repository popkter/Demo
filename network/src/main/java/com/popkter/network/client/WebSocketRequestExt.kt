package com.popkter.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

/**
 * 进行WebSocket链接请求
 */
object WebSocketRequestExt : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    private const val TAG = "WebSocketRequestExt"

    @JvmStatic
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
    }

    @JvmStatic
    private val webSocketSessions = mutableMapOf<String, DefaultClientWebSocketSession>()


    @JvmStatic
    private val webSocketJobs = mutableMapOf<String, Job>()


    /**
     * 连接到WebSocket
     * [url] WebSocket 地址
     * [sessionId] 会话ID
     * [onResponse] 消息响应
     */
    fun buildSession(
        url: String,
        sessionId: String,
        onResponse: (String) -> Unit = {}
    ) {
        val job = launch {
            val session = client.webSocketSession(urlString = url) {
                println("Connected to $sessionId")
            }
            webSocketSessions[sessionId] = session

            try {

                // 发送一条消息
                session.send(Frame.Text("""{"session_id":"$sessionId"}"""))


                for (message in session.incoming) {
                    when (message) {
                        is Frame.Text -> {
                            onResponse(message.readText())
//                            Log.d(TAG, "Received Text from $sessionId: ${message.readText()}")
                        }

                        else -> {
//                            Log.d(TAG, "Received from $sessionId: $message")
                        }
                    }
                }
            } catch (e: Exception) {
//                Log.e(TAG, "Error on $sessionId message: ${e.message}")
            }
        }

        webSocketJobs[sessionId] = job
    }

    /**
     * 断开指定的链接
     * [serverName] 服务器名称
     */
    fun disconnectSession(sessionId: String) {
        launch {
            webSocketSessions[sessionId]?.run {
                close(CloseReason(CloseReason.Codes.NORMAL, "Disconnect $sessionId"))
//                Log.d(TAG, "Disconnected from $sessionId")
                webSocketSessions.remove(sessionId)
            } ?: {
//                Log.e(TAG, "No active WebSocket connection found for $sessionId")
            }
            webSocketJobs[sessionId]?.cancel()
        }
    }

    /**
     * 直接关闭Client
     */
    fun closeAllSession() {
        webSocketSessions.clear()
        client.close()
        coroutineContext.cancelChildren()
    }

}

