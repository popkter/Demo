package com.popkter.network.client

import com.google.gson.GsonBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

/**
 * @author Pop.Yu
 * @date 2024/9/5
 * @description Ktor网络请求管理类
 */
object HttpRequestExt : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    const val TAG = "HttpRequestExt`"

    val mClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 10000 // 请求超时时间
            connectTimeoutMillis = 10000 // 连接超时时间
            socketTimeoutMillis = 10000 // 读写超时时间
        }
//        install(Logging) {
            //自定义输出日志
            /*            logger = object : Logger {
                            override fun log(message: String) {
                                Log.e(TAG, "LogInfo: $message", )
                            }
                        }*/
//            logger = Logger.SIMPLE
//            level = LogLevel.ALL
//        }
    }

    /**
     * 通用GET请求
     * @param url 请求地址
     * @param header 请求头
     * @return T? 期待返回的响应体类型
     * @param onError 自定义错误处理逻辑
     */
    suspend inline fun <reified T> doGetRequest(
        url: String,
        header: Map<String, String> = emptyMap(),
        gsonBuilder: GsonBuilder = GsonBuilder().setLenient(),
        onError: (Throwable) -> T? = { null }
    ): T? {

        return runCatching {
            // 如果 T 是 String 类型，直接返回 responseBody
            val responseBody = mClient.get(url) {
                headers {
                    header.forEach {
                        append(it.key, it.value)
                    }
                }
            }.bodyAsText()

//            Log.d(TAG, "doGetRequest Success: $responseBody")

            if (T::class == String::class) {
                return responseBody as T
            }

            // 否则使用 Gson 解析
            val gson = gsonBuilder.create()
            val response = gson.fromJson(responseBody, T::class.java)
            response
        }.onFailure {
//            Log.d(TAG, "doGetRequest Error: ${it.message}")
            onError(it)
        }.getOrNull()
    }

    /**
     * 通用POST请求
     * @param url 请求地址
     * @param header 请求头
     * @param requestBody 请求体
     * @param contentType Content-Type
     * @param gsonBuilder 自定义GsonBuilder
     * @param onError 自定义错误处理逻辑
     * @return T? 期待返回的响应体类型
     */
    suspend inline fun <reified T> doPostRequest(
        url: String,
        header: Map<String, String> = emptyMap(),
        requestBody: Any?,
        contentType: String? = null,
        gsonBuilder: GsonBuilder = GsonBuilder().setLenient(),
        onError: (Throwable) -> T? = { null }
    ): T? {
        return runCatching {
            val responseBody = mClient.post(url) {
                headers {
                    header.forEach {
                        append(it.key, it.value)
                    }
                }

                contentType?.let { contentType(ContentType.parse(it)) }

                requestBody?.let {
                    setBody(it)
                }
            }.bodyAsText()
//            Log.d(TAG, "doPostRequest Success: $responseBody")
            // 如果 T 是 String 类型，直接返回 responseBody
            if (T::class == String::class) {
                return responseBody as T
            }

            // 否则使用 Gson 解析
            val gson = gsonBuilder.create()
            val response = gson.fromJson(responseBody, T::class.java)
            response
        }.onFailure {
//            Log.d(TAG, "doPostRequest Error: ${it.message}")
            onError(it)
        }.getOrNull()
    }

    /**
     * 流式POST请求
     * @param url 请求地址
     * @param header 请求头
     * @param requestBody 请求体
     * @param contentType Content-Type
     * @param onResponse 响应
     */
    suspend inline fun doPostStreamRequest(
        url: String,
        header: Map<String, String> = emptyMap(),
        requestBody: Any?,
        contentType: String? = null,
        crossinline onResponse: (RequestStatus, String) -> Unit = { status, line -> }
    ) {

//        Log.d(TAG, "doPostStreamRequest start requestBody: $requestBody")
        runCatching {
            mClient.post(url) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    header.forEach {
                        append(it.key, it.value)
                    }
                }
                contentType?.let { contentType(ContentType.parse(it)) }
                // 配置请求体
                setBody(requestBody)
            }.bodyAsChannel().apply {
                while (!isClosedForRead) {
                    val buffer = readUTF8Line()
                    if (buffer?.isNotEmpty() == true) {
//                        Log.e(TAG, "doPostStreamRequest partial result: $buffer")
                        delay(30)
                        onResponse(RequestStatus.SUCCESS, buffer)
                    }
                }
                onResponse(RequestStatus.FINISH, "")
            }
        }.onFailure {
            onResponse.invoke(RequestStatus.ERROR, it.message.toString())
//            Log.d(TAG, "doPostStreamRequest Error: ${it.message}")
        }
    }

    /**
     * 流式GET请求
     * @param url 请求地址
     * @param header 请求头
     * @param onResponse 响应
     */
    suspend inline fun doGetStreamRequest(
        url: String,
        header: Map<String, String> = emptyMap(),
        crossinline onResponse: (RequestStatus, String) -> Unit = { status, line -> }
    ) {
        runCatching {
            mClient.get(url) {
                headers {
                    header.forEach {
                        append(it.key, it.value)
                    }
                }
            }.bodyAsChannel().apply {
//                Log.e(TAG, "doGetStreamRequest: $this")
                while (!isClosedForRead) {
                    val buffer = readUTF8Line()
                    if (buffer?.isNotEmpty() == true) {
//                        Log.e(TAG, "doGetStreamRequest partial result: $buffer")
                        delay(30)
                        onResponse(RequestStatus.SUCCESS, buffer)
                    }
                }
                onResponse(RequestStatus.FINISH, "")
            }
        }.onFailure {
            onResponse.invoke(RequestStatus.ERROR, it.message.toString())
//            Log.d(TAG, "doGetStreamRequest Error: ${it.message}")
        }
    }
}

