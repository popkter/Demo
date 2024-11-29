package com.popkter.voice_assistant.impl.edge_tts

import android.content.Context
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.popkter.voice_assistant.base.BaseTtsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class EdgeTtsHelper : BaseTtsHelper(),
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    companion object {
        val INSTANCE by lazy { EdgeTtsHelper() }
    }

    private val processing = Mutex(false)

    private val _ttsBuffer = StringBuffer()

    private val sentenceEndRegex = Regex(".*?[，。？！,?!]")

    private lateinit var ttsPlayer: ExoPlayer

    private var ttsVoice: EdgeTtsVoice = EdgeTtsVoice.XIAO_XIAO

    override fun initTts(context: Context): EdgeTtsHelper {
        ttsPlayer = ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.d(TAG, "onPlaybackStateChanged: $playbackState")
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            clearMediaItems()
                            prepare()
                        }

                        Player.STATE_ENDED -> {
                            clearMediaItems()
                            prepare()
                        }

                        else -> {}
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        if (!hasPreviousMediaItem()) {
                            onTtsStart()
                        } else {
                            onTtsResume()
                        }
                    } else {
                        if (!hasNextMediaItem() || _ttsBuffer.isEmpty()) {
                            onTtsEnd()
                        } else {
                            onTtsPause()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    onTtsError(error.errorCode, error.message ?: "")
                }
            })
            setAudioAttributes(
                // 设置音频属性为 TTS 通道
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA) // TTS用途
                    .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH) // 内容类型为语音
                    .build(),
                true
            )
        }
        return this
    }

    override fun playTts(text: String) {
        launch {
            text.split("?", "[", "，", "。", "？", "！", "?", "!", "]").forEach {
                requestTts(it)
            }
        }
    }

    override fun playChunkTts(appendText: String) {
        Log.e(TAG, "playChunkTts: appendText: $appendText")
        launch {
            processing.withLock {
                _ttsBuffer.append(appendText)
                val matchResult = sentenceEndRegex.find(_ttsBuffer)
                if (matchResult != null) {
                    // 取出完整的句子
                    val sentence = matchResult.value.trim()
                    Log.e(TAG, "playChunkTts: sentence: $sentence")
                    requestTts(sentence)
                    // 从缓冲区中删除已处理的句子
                    _ttsBuffer.delete(0, matchResult.range.last + 1)
                }
            }
        }
    }

    override fun pauseTts() {
        launch(Dispatchers.Main) {
            ttsPlayer.pause()
        }
    }

    override fun resumeTts() {
        launch(Dispatchers.Main) {
            ttsPlayer.play()
        }
    }

    override fun cancelTts() {
        ttsPlayer.stop()
        _ttsBuffer.setLength(0)
    }

    override fun stopTts(immediately: Boolean) {
        ttsPlayer.stop()
        _ttsBuffer.setLength(0)
    }

    override fun releaseTts() {
        stopTts(true)
        ttsPlayer.release()
    }

    fun updateVoiceType(voice: EdgeTtsVoice) {
        ttsVoice = voice
    }

    fun updateVoiceType(name: String) {
        ttsVoice.name = name
    }


    private suspend fun requestTts(text: String) {
        Log.e(TAG, "requestTts: $text")
        coroutineScope {
            withContext(Dispatchers.IO) {
                val query = URLEncoder.encode(text, "UTF-8")
                withContext(Dispatchers.Main) {
                    ttsPlayer.addMediaItem(MediaItem.fromUri("http://124.221.124.238:10010/stream_audio?text=$query&voice=${ttsVoice.name}&rate=30&volume=0"))
                }
            }
        }
    }

    sealed class EdgeTtsVoice(var name: String) {
        data object XIAO_XIAO : EdgeTtsVoice("zh-CN-XiaoxiaoNeural")
        data object XIAO_YI : EdgeTtsVoice("zh-CN-XiaoyiNeural")
        data object YUN_JIAN : EdgeTtsVoice("zh-CN-YunjianNeural")
        data object YUN_XI : EdgeTtsVoice("zh-CN-YunxiNeural")
        data object YUN_XIA : EdgeTtsVoice("zh-CN-YunxiaNeural")
        data object YUN_YANG : EdgeTtsVoice("zh-CN-YunyangNeural")
        data object LIAO_NING : EdgeTtsVoice("zh-CN-liaoning-XiaobeiNeural")
        data object SHA_AN_XI : EdgeTtsVoice("zh-CN-shaanxi-XiaoniNeural")

        data object HUI_GAAI : EdgeTtsVoice("zh-HK-HiuGaaiNeural")
        data object HIU_MAAM : EdgeTtsVoice("zh-HK-HiuMaanNeural")
        data object WAN_LUNG : EdgeTtsVoice("zh-HK-WanLungNeural")
        data object HSIAO_CHEN : EdgeTtsVoice("zh-TW-HsiaoChenNeural")
        data object HSIAO_YU : EdgeTtsVoice("zh-TW-HsiaoYuNeural")
        data object YUN_JEH : EdgeTtsVoice("zh-TW-YunJheNeural")
    }
}