package com.popkter.network.server

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.*

class KtorServerExt private constructor(){

    companion object {
        val INSTANCE by lazy { KtorServerExt() }
        private const val TAG = "KtorServer"
    }

    private var server: CIOApplicationEngine? = null

    fun startServer() {
        CoroutineScope(Dispatchers.IO).launch {
            server = embeddedServer(
                CIO,
                port = 8086,
                host = "0.0.0.0"
            ) { module() }.start(wait = false) as CIOApplicationEngine
//            Log.d(TAG, "服务器已启动")
        }
    }

    fun stopServer() {
        server?.stop(1000, 2000)
//        Log.d(TAG, "服务器已停止")
    }

    private fun Application.module() {
        routing {
            get("/") {
                call.respondText("Hello, Ktor on Android!")
            }
            post("/data") {
                val receivedText = call.receiveText()
//                Log.d(TAG, "收到数据：$receivedText")
                call.respondText("数据已接收")
            }
        }
    }
}
