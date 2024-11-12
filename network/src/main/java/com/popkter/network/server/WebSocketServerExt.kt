package com.popkter.network.server

import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * @desc  : 基于Ktor的 WebSocket 服务器
 * @date  : 2024/10/8
 * @author: Pop
 */
class WebSocketServerExt private constructor() : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    companion object {
        val INSTANCE by lazy { WebSocketServerExt() }
        private const val TAG = "KtorWebSocketServer"
    }

    private var server: CIOApplicationEngine? = null

    private val sessions = mutableListOf<WebSocketSession>()

    private var sendJob: Job? = null

    val messageFlow: MutableSharedFlow<String> = MutableSharedFlow()

    /**
     * 启动服务器
     */
    fun startServer() {
//        Log.d(TAG, "start websocket")
        launch {
            server = embeddedServer(CIO, port = 8087, host = "0.0.0.0") {
                install(WebSockets) {
                    pingPeriod = 10.seconds
                    timeout = 20.seconds
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
                routing {
                    webSocket("/ws") {
                        sessions.add(this)
//                        Log.d(TAG, "客户端已连接")
//                        send("欢迎连接到 WebSocket 服务器！")
                        try {
                            // 处理客户端发送的消息
                            for (frame in incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        val receivedText = frame.readText()
//                                        Log.d(TAG, "收到客户端消息：$receivedText")
                                        messageFlow.emit(receivedText) // 发送消息到 Flow
                                    }

                                    else -> {}
                                }
                            }
                        } catch (e: ClosedReceiveChannelException) {
                            // 客户端主动断开连接
//                            Log.d(TAG, "客户端主动断开连接：${e}")
                        } catch (e: Exception) {
                            // 处理其他异常
//                            Log.d(TAG, "处理 WebSocket 消息时发生异常: ${e.message}")
                        } finally {
                            sessions.remove(this)
//                            Log.d(TAG, "客户端已断开连接")
                        }
                    }
                }
            }.start(wait = false) as CIOApplicationEngine
//            Log.d(TAG, "WebSocket 服务器已启动")
        }
    }

    /**
     * 广播消息给所有客户端
     * @param message 消息
     */
    fun broadcastMessage(message: String) {
//        Log.d(TAG, "broadcastMessage:${message}")
        launch {
            sessions.forEach {
                it.send(message)
            }
            cancel()
        }
    }

    /**
     * 循环广播消息给所有客户端
     * @param period 发送消息间隔, 单位毫秒, 默认500ms
     * @param action 消息生成函数
     */
    fun broadcastMessageInterval(period: Long = 500L, action: () -> String) {
        sendJob = launch {
            while (true) {
                delay(period)
                sessions.forEach { it.send(action()) }
            }
        }
    }

    /**
     * 停止服务器
     */
    fun stopServer() {
        sendJob?.cancel()
        server?.stop(1000, 2000)
//        Log.d(TAG, "服务器已停止")
    }

}
