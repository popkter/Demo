package com.popkter.collector

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.google.gson.Gson
import com.popkter.common.application_ext.ApplicationModule
import com.popkter.media.MediaPlayerExt
import com.popkter.network.client.HttpRequestExt
import com.senseauto.basiclibrary.entity.Poi
import com.senseauto.basiclibrary.entity.WeatherResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

class MainViewModel : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO + Job()) {

    private val _text = StringBuffer("你好我是小娜")

    private val novelResultFlow = MutableSharedFlow<String>()
    val novelResult = this.novelResultFlow.asSharedFlow()


    private val imageResultFlow = MutableSharedFlow<String>()
    val imageResult = imageResultFlow.asSharedFlow()

    private val weatherResult = MutableSharedFlow<WeatherResponse?>()
    val weatherResultFlow = weatherResult.asSharedFlow()

    private val poiResult = MutableSharedFlow<List<Poi>>()
    val poiResultFlow = poiResult.asSharedFlow()

    private val musicResultFlow = MutableSharedFlow<String>()
    val musicResult = musicResultFlow.asSharedFlow()


    private val musicProgressFlow = MutableSharedFlow<Long>(replay = 2)
    val musicProgress = musicProgressFlow.asSharedFlow()

    private val musicDurationFlow = MutableSharedFlow<Long>()
    val musicDuration = musicDurationFlow.asSharedFlow()

    private val musicPlayStatusFlow = MutableSharedFlow<Int>()
    val musicPlayStatus = musicPlayStatusFlow.asSharedFlow()

    private val showMusicCardFlow = MutableStateFlow(false)
    val showMusicCard = showMusicCardFlow.asStateFlow()

    private val showMusicInWidgetFlow = MutableStateFlow(false)
    val showMusicInWidget = showMusicInWidgetFlow.asStateFlow()

    private val musicInfoFlow = MutableSharedFlow<String>()
    val musicInfo = musicInfoFlow.asSharedFlow()

    private var requestJob: Job? = null

    private val gson = Gson()


    private val mediaPlayer = MediaPlayerExt(ApplicationModule.application).apply {
        loadListener = object : MediaPlayerExt.MusicLoader {
            override suspend fun onLoadPrevious(): MediaItem {
                return MediaItem.Builder()
                    .setUri(musicList[max(0, this@apply.currentMediaItemIndex - 1)]).build()
            }

            override suspend fun onLoadNext(): MediaItem {
                return MediaItem.Builder()
                    .setUri(musicList[minOf(this@apply.currentMediaItemIndex + 1, 2)]).build()
            }

        }
        playerStateListener = object : MediaPlayerExt.PlayStateChangeListener() {

            override fun onPlayItemChange(item: MediaItem) {
                launch {
                    musicInfoFlow.emit(item.mediaMetadata.title.toString())
                }
                /*binding.title.apply {
                    text = item.mediaMetadata.title
                    isSelected = true
                }*/
            }

            override fun onPlayProgressChange(progress: Long, duration: Long) {
                launch {
                    musicProgressFlow.emit(progress)
                    musicDurationFlow.emit(duration)
                }
            }

            override fun onStatePlaying() {
                launch {
                    musicPlayStatusFlow.emit(MUSIC_PLAYING)
                }
            }

            override fun onStateLoading() {
                launch {
                    musicPlayStatusFlow.emit(MUSIC_LOADING)
                }
            }

            override fun onStatePausing() {
                launch {
                    musicPlayStatusFlow.emit(MUSIC_PAUSE)
                }
            }

            override fun onStateCompleted() {
                launch {
                    musicPlayStatusFlow.emit(MUSIC_PAUSE)
                }
            }
        }
    }

    private suspend fun loadDemoThink() {
        resetDataInternal()
        THINK_DESC.forEach {
            _text.append(it)
            novelResultFlow.emit(_text.toString())
            delay(20)
        }
        delay(1000)
        resetDataInternal()
    }

    fun loadNovelDemoData() {
        requestJob?.cancel()
        requestJob = launch {
            loadDemoThink()
            NOVEL_DATA_LIST.random().forEach {
                _text.append(it)
                novelResultFlow.emit(_text.toString())
                delay(20)
            }
        }
    }

    fun loadSceneDemoData() {
        requestJob?.cancel()
        requestJob = launch {
            loadDemoThink()
            SCENE_DESC.forEach {
                _text.append(it)
                novelResultFlow.emit(_text.toString())
                delay(20)
            }
            imageResultFlow.emit(MUSIC_ITEM_COVER)
        }
    }

    fun loadPoiDemoData() {
        requestJob?.cancel()
        requestJob = launch {
            loadDemoThink()
            poiResult.emit(POI_LIST)
        }
    }

    fun loadWeatherDemoData() {
        requestJob?.cancel()
        requestJob = launch {
            loadDemoThink()
            novelResultFlow.emit("本周温度逐渐升高，从21.5°C升至25.8°C，随后略有下降。早晚温差较大，建议携带薄外套，白天适宜轻便衣物。整体温度适中。")
            val result = Gson().fromJson(WEATHER_DATA, WeatherResponse::class.java)
            weatherResult.emit(result)
        }
    }

    fun loadMusicDemoData() {
        requestJob?.cancel()
        launch {
            if (showMusicInWidgetFlow.value) {
                resetDataInternal()
            } else {
                loadDemoThink()
            }
            showMusicCardFlow.emit(true)
            showMusicInWidgetFlow.emit(false)
            play()
        }
    }

    fun playPrevious() {
        mediaPlayer.playPrevious()
    }

    fun playNext() {
        mediaPlayer.playNext()
    }

    fun playPause() {
        launch(Dispatchers.Main) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.play()
            }
        }
    }

    fun pause() {
        launch(Dispatchers.Main) {
            musicPlayStatusFlow.emit(MUSIC_PAUSE)
            mediaPlayer.pause()
        }
    }

    fun play(autoLoad: Boolean = false) {
        launch(Dispatchers.Main) {
            if (autoLoad) {
                mediaPlayer.play()
            } else {
                mediaPlayer.apply {
                    playWhenReady = true
                    prepare()
                }
            }
        }
    }


    fun seekTo(it: Long = -1, isFinish: Boolean = false) {
        launch {
            withContext(Dispatchers.Main) {
                if (it != -1L) {
                    musicProgressFlow.emit(it)
                }
                if (isFinish) {
                    if (musicProgress.replayCache.isNotEmpty()) {
                        mediaPlayer.seekTo(musicProgress.replayCache.first())
                    }
                }
            }
        }
    }

    var currentMediaItem = mediaPlayer.currentMediaItem

    fun loadMusicFromLocal() {
        val directory = File(MUSIC_DIRECTORY)
        if (directory.isDirectory) {
            directory.listFiles()?.map { file ->
                if (file.extension == "mp3") {
                    mediaPlayer.addMediaItem(
                        MediaItem.Builder().setUri(Uri.fromFile(file)).setMediaMetadata(
                            MediaMetadata.Builder().setTitle(file.name.replace(".128", "")).build()
                        ).build()
                    )
                }
            }
        }
    }

    private suspend fun resetDataInternal() {
        _text.setLength(0)
        if (showMusicCardFlow.value) {
            showMusicInWidgetFlow.emit(true)
        }
        showMusicCardFlow.emit(false)
        weatherResult.emit(null)
        novelResultFlow.emit("")
        imageResultFlow.emit("")
        poiResult.emit(emptyList())
    }

    fun resetData() {
        Log.e(TAG, "resetData: ")
        requestJob?.cancel()
        launch {
            resetDataInternal()
        }
    }

    fun loadNovelData() {
        val memoryInfo = emptyList<Map<String, Any?>>()
        requestJob?.cancel()
        requestJob = launch {
            resetDataInternal()
            THINK_DESC.forEach {
                _text.append(it)
                novelResultFlow.emit(_text.toString())
                delay(20)
            }

            val params = mapOf(
                "user_query" to "讲一个奇幻冒险的长故事",
                "tools" to "",
                "role" to "user",
                "memory_info" to memoryInfo,
                "stream" to true
            )
            val gson = Gson()
            val openSessionJsonString = gson.toJson(params)

            _text.setLength(0)
            HttpRequestExt.doPostStreamRequest(
                "http://58.22.103.21:18800/novel/agent/chat",
                header = mapOf("Content-Type" to "application/json"),
                openSessionJsonString
            ) { _, line ->
                launch {
                    runCatching {
                        val result = gson.fromJson(
                            line.substringAfter("data:").trim(),
                            ChatResponseData::class.java
                        )
                        _text.append(result.data?.content)
                        novelResultFlow.emit(_text.toString())
                    }.onFailure {
                        Log.e(TAG, "loadNovelData: ${it.message}")
                    }
                }
            }
        }
    }

    fun generateAudio() {
        // TODO:

    }
    fun requestAudio(text: String) {

        launch(Dispatchers.IO) {

            val tempFile =  File.createTempFile("temp_audio", "temp", ApplicationModule.application.cacheDir)

            val response = HttpRequestExt.mClient.post("http://124.221.124.238:10010/synthesize") {
                contentType(ContentType.Application.Json)
                // 配置请求体
                setBody(
                    gson.toJson(
                        mapOf(
                            "text" to text,
                            "voice" to "Female-XiaoxiaoNeural",
                            "rate" to 12,
                            "volume" to 0
                        )
                    )
                )
            }

            FileOutputStream(tempFile).use { outputStream ->
                val byteChannel: ByteReadChannel = response.bodyAsChannel()
                val buffer = ByteArray(4096)
                while (!byteChannel.isClosedForRead) {
                    val bytesRead = byteChannel.readAvailable(buffer)
                    if (bytesRead > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
                withContext(Dispatchers.Main) {
                    mediaPlayer.addMediaItem(MediaItem.fromUri(tempFile.toUri()))
                    play(false)
                }
            }

        }
    }

    companion object {
        const val TAG = "MainViewModel"

        private const val MUSIC_DIRECTORY = "/sdcard/Music/"

        private val musicList = mutableListOf(
            "https://psv4.vkuseraudio.net/s/v1/amp2/h82W4w6RALV5Rwia55-VX-xWi2TdH_IpDNZj4r8GUxfbqpCOtyqAnZvcWlGE50DvphnOToUwS9UU9AnU0a9HPalPfqBil9Lbd7zfpTiSwM1K1c7M-uZYB_X6uxzGFcA9Fc9Dxg1dzKRo6atIDhKseKsbowUJ85PXpwIR.mp3?siren=1",
            "https://psv4.vkuseraudio.net/s/v1/amp2/QIopy-p5OMu7lt5JZUDh2Q_NtfRLAikmvwZ1SSsYGvivP8G6VPEZucqE0UhEdyDjf7bYZ_k76-N5h7Q4djsN5KuYCfALKVpREQ4XcghD_q5mqD0dYp-wx-Sobu-s3nZxpurdCxS0hnwZOBCNOIBwOKIsuvc-_Cy4gskk.mp3?siren=1",
            "https://psv4.vkuseraudio.net/s/v1/amp2/Tk4HXRL8IuvnVublfnkLgmDtoPlMJiWLVefxnR6vurl8kYSG-LMcVUkLyeYAQHjDop7VjMSFg_136-j-xlJuuoSmxxQe1IetR0FB53clOJ_5LBomfmuIgJcRwuhBUJ54d5t1_CoP9HRMe8RTwo3mLR5EGVLTB2uw8IoY.mp3?siren=1",
        )
        const val MUSIC_LOADING = 0
        const val MUSIC_PLAYING = 1
        const val MUSIC_PAUSE = 2
    }
}

data class ChatData(
    val type: String,
    val content: String
)

data class ChatResponseData(
    val code: Int,
    val message: String,
    val data: ChatData?,
    val domain: String
)

