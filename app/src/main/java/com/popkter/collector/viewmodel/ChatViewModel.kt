package com.popkter.collector.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.google.gson.Gson
import com.popkter.collector.R
import com.popkter.collector.THINK_DESC
import com.popkter.collector.domain.WeatherAgent
import com.popkter.collector.entity.Poi
import com.popkter.collector.model.DaysWeather
import com.popkter.common.application_ext.ApplicationModule
import com.popkter.media.MediaPlayerExt
import com.popkter.voice_assistant.VuiStatementHelper
import com.popkter.voice_assistant.impl.unisound.asr.UniSoundAsrHelper
import com.popkter.voice_assistant.base.BaseAsrHelper
import com.popkter.voice_assistant.impl.edge_tts.EdgeTtsHelper
import com.popkter.voice_assistant.impl.unisound.tts.UniSoundTtsHelper
import com.popkter.voice_assistant.impl.unisound.wake_up.UniSoundWakeUpHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

class ChatViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"

        private val musicList = mutableListOf(
            "https://psv4.vkuseraudio.net/s/v1/amp2/h82W4w6RALV5Rwia55-VX-xWi2TdH_IpDNZj4r8GUxfbqpCOtyqAnZvcWlGE50DvphnOToUwS9UU9AnU0a9HPalPfqBil9Lbd7zfpTiSwM1K1c7M-uZYB_X6uxzGFcA9Fc9Dxg1dzKRo6atIDhKseKsbowUJ85PXpwIR.mp3?siren=1",
            "https://psv4.vkuseraudio.net/s/v1/amp2/QIopy-p5OMu7lt5JZUDh2Q_NtfRLAikmvwZ1SSsYGvivP8G6VPEZucqE0UhEdyDjf7bYZ_k76-N5h7Q4djsN5KuYCfALKVpREQ4XcghD_q5mqD0dYp-wx-Sobu-s3nZxpurdCxS0hnwZOBCNOIBwOKIsuvc-_Cy4gskk.mp3?siren=1",
            "https://psv4.vkuseraudio.net/s/v1/amp2/Tk4HXRL8IuvnVublfnkLgmDtoPlMJiWLVefxnR6vurl8kYSG-LMcVUkLyeYAQHjDop7VjMSFg_136-j-xlJuuoSmxxQe1IetR0FB53clOJ_5LBomfmuIgJcRwuhBUJ54d5t1_CoP9HRMe8RTwo3mLR5EGVLTB2uw8IoY.mp3?siren=1",
        )

        private val greeting = mutableListOf(
            R.raw.wake_up_res_1,
            R.raw.wake_up_res_2,
            R.raw.wake_up_res_3,
            R.raw.wake_up_res_4,
        )
        const val MUSIC_LOADING = 0
        const val MUSIC_PLAYING = 1
        const val MUSIC_PAUSE = 2
    }

    private val _text = StringBuffer("你好我是小娜")

    //文字上屏
    private val novelResultFlow = MutableSharedFlow<String>()
    val novelResult = this.novelResultFlow.asSharedFlow()

    //图片上屏
    private val imageResultFlow = MutableSharedFlow<String>()
    val imageResult = imageResultFlow.asSharedFlow()

    //天气上屏
    private val weatherResultFlow = MutableSharedFlow<List<Pair<Float, String>>?>()
    val weatherResult = weatherResultFlow.asSharedFlow()

    //地点上屏
    private val poiResult = MutableSharedFlow<List<Poi>>()
    val poiResultFlow = poiResult.asSharedFlow()

    //音乐上屏
    private val musicResultFlow = MutableSharedFlow<String>()
    val musicResult = musicResultFlow.asSharedFlow()

    //音乐进度
    private val musicProgressFlow = MutableSharedFlow<Long>(replay = 2)
    val musicProgress = musicProgressFlow.asSharedFlow()

    //音乐时长
    private val musicDurationFlow = MutableSharedFlow<Long>()
    val musicDuration = musicDurationFlow.asSharedFlow()

    //播放状态
    private val musicPlayStatusFlow = MutableSharedFlow<Int>()
    val musicPlayStatus = musicPlayStatusFlow.asSharedFlow()

    //音乐卡片
    private val showMusicCardFlow = MutableStateFlow(false)
    val showMusicCard = showMusicCardFlow.asStateFlow()

    //音乐组件
    private val showMusicInWidgetFlow = MutableStateFlow(false)
    val showMusicInWidget = showMusicInWidgetFlow.asStateFlow()

    //音乐信息
    private val musicInfoFlow = MutableSharedFlow<String>()
    val musicInfo = musicInfoFlow.asSharedFlow()

    //请求状态
    private var requestJob: Job? = null

    private val gson = Gson()

    //UniSoundTts
    private val uniUniSoundTtsHelper =
        UniSoundTtsHelper.INSTANCE.initTts(ApplicationModule.application)

    //EdgeTts
    private val edgeTtsHelper = EdgeTtsHelper.INSTANCE.initTts(ApplicationModule.application)

    //UniSoundWakeup
    private val uniSoundWakeUpHelper =
        UniSoundWakeUpHelper.INSTANCE.initWakeUp(ApplicationModule.application)

    //UniSoundAsr
    private val uniSoundAsrHelper = UniSoundAsrHelper.INSTANCE.initAsr(ApplicationModule.application)

    //播放器
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

    fun initVui() {
        Log.e(TAG, "initVui: ")

        VuiStatementHelper.unregisterVuiStatementListeners()
        VuiStatementHelper.registerVuiStatementListeners(
            onWakeUp = {
                Log.e(TAG, "initVui: onWakeUp")
                resetData()
                stopTts()
                uniSoundAsrHelper.stopAsr(false)
                wakeupResponse()
                startAsr()
            },
            onAsrProcessing = { asr ->
                Log.e(TAG, "initVui: onAsrProcessing $asr")
                viewModelScope.launch {
                    novelResultFlow.emit(asr)
                }
            },
            onAsrResult = { result ->
                Log.e(TAG, "initVui: onAsrResult $result")
                viewModelScope.launch {
                    novelResultFlow.emit(result)
                    delay(2000)
                    playTts(result, TtsEngineType.EdgeTts)
                }
            },
            onTtsStart = {
                Log.e(TAG, "initVui: onTtsStart")

            },
            onTtsEnd = {
                Log.e(TAG, "initVui: onTtsEnd")
            }
        )
    }

    fun startWakeUp() {
        uniSoundWakeUpHelper.startWakeUp()
    }

    fun updateDuplexTimeout(timeOut: BaseAsrHelper.DuplexTimeout) {
        uniSoundAsrHelper.toggleFullDuplex(timeOut)
    }

    fun updateVoiceType(name: String, engine: TtsEngineType = TtsEngineType.UniSoundTts) {
        when (engine) {
            TtsEngineType.UniSoundTts -> {
                uniUniSoundTtsHelper.updateVoiceType(name)
            }

            TtsEngineType.EdgeTts -> {
                edgeTtsHelper.updateVoiceType(name)
            }
        }
    }

    fun playTts(text: String, engine: TtsEngineType = TtsEngineType.UniSoundTts) {
        when (engine) {
            TtsEngineType.UniSoundTts -> {
                uniUniSoundTtsHelper.playTts(text)
            }

            TtsEngineType.EdgeTts -> {
                edgeTtsHelper.playTts(text)
            }
        }
    }

    fun playChunkTts(appendText: String, engine: TtsEngineType = TtsEngineType.UniSoundTts) {
        when (engine) {
            TtsEngineType.UniSoundTts -> {
                uniUniSoundTtsHelper.playChunkTts(appendText)
            }

            TtsEngineType.EdgeTts -> {
                edgeTtsHelper.playChunkTts(appendText)
            }
        }
    }

    private fun stopTts(){
        uniUniSoundTtsHelper.stopTts(true)
        edgeTtsHelper.stopTts(true)
    }

    private fun startAsr() {
        uniSoundAsrHelper.startAsr()
    }

    fun resetData() {
        Log.e(TAG, "resetData: ")
        requestJob?.cancel()
        viewModelScope.launch {
            resetDataInternal()
        }
    }

    fun playPrevious() {
        mediaPlayer.playPrevious()
    }

    fun playNext() {
        mediaPlayer.playNext()
    }

    fun playPause() {
        viewModelScope.launch(Dispatchers.Main) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.play()
            }
        }
    }

    fun pause() {
        viewModelScope.launch(Dispatchers.Main) {
            musicPlayStatusFlow.emit(MainViewModel.MUSIC_PAUSE)
            mediaPlayer.pause()
        }
    }

    fun play(autoLoad: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
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
        viewModelScope.launch {
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

    private suspend fun loadDemoThink() {
        resetDataInternal()
        THINK_DESC.forEach {
            _text.append(it)
            novelResultFlow.emit(_text.toString())
            delay(20)
        }
//        delay(10000)
    }

    private suspend fun resetDataInternal() {
        _text.setLength(0)
        if (showMusicCardFlow.value) {
            showMusicInWidgetFlow.emit(true)
        }
        showMusicCardFlow.emit(false)
        weatherResultFlow.emit(null)
        novelResultFlow.emit("")
        imageResultFlow.emit("")
        poiResult.emit(emptyList())
    }

    private val wakeupResponsePlayer = MediaPlayer()


    fun loadWeather(query: String) {
        viewModelScope.launch {
//            loadDemoThink()
            resetDataInternal()
            WeatherAgent.loadWeatherData(query,
                onDaysData = {
                    launch {
                        weatherResultFlow.emit(it.map { it.temperature.avg.toFloat() to it.date })
                    }
                    Log.e(TAG, "onDaysData: $it")
                },
                onHoursData = {
                    launch {
                        weatherResultFlow.emit(it.map { it.temp.toFloat() to it.datetime})
                    }
                    Log.e(TAG, "onHoursData: ", )
                },
                onSummaryUpdate = {
                    launch {
                        _text.append(it)
                        novelResultFlow.emit(_text.toString())
                        playChunkTts(it, TtsEngineType.UniSoundTts)
                    }
                    Log.e(TAG, "onSummaryUpdate: ")
                })
        }
    }

    private fun wakeupResponse(){
        greeting.random().let {
            wakeupResponsePlayer.reset()
            val afd = ApplicationModule.application.resources.openRawResourceFd(it)
            wakeupResponsePlayer.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
            wakeupResponsePlayer.prepare()
            wakeupResponsePlayer.start()
            /*wakeupResponsePlayer.setOnCompletionListener {
                startAsr()
            }*/
        }
    }

    override fun onCleared() {
        super.onCleared()
        uniSoundAsrHelper.releaseAsr()
        uniSoundWakeUpHelper.releaseWakeUp()
        uniUniSoundTtsHelper.resumeTts()
        edgeTtsHelper.releaseTts()
    }

    sealed class TtsEngineType {
        data object UniSoundTts : TtsEngineType()
        data object EdgeTts : TtsEngineType()
    }

}