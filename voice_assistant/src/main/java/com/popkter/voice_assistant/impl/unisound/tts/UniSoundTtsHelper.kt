package com.popkter.voice_assistant.impl.unisound.tts

import android.content.Context
import android.util.Log
import com.popkter.voice_assistant.BuildConfig
import com.popkter.voice_assistant.base.BaseTtsHelper
import com.unisound.sdk.tts.TtsEvent
import com.unisound.sdk.tts.TtsOption
import com.unisound.sdk.tts.TtsPlayOption
import com.unisound.sdk.tts.UnisoundTtsEngine
import com.unisound.sdk.tts.impl.ITtsEventListener
import com.unisound.sdk.tts.param.UnisoundTtsMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ConcurrentLinkedQueue


class UniSoundTtsHelper : ITtsEventListener,
    CoroutineScope by CoroutineScope(Dispatchers.IO), BaseTtsHelper() {

    companion object {
        val INSTANCE by lazy { UniSoundTtsHelper() }
    }

    override val TAG: String
        get() = "TtsManager"

    private val _ttsBuffer = StringBuilder()
    private val ttsQueue = ConcurrentLinkedQueue<String>()

    private val sentenceEndRegex = Regex(".*?[，。？！,?!]")

    private lateinit var uniSoundTtsEngine: UnisoundTtsEngine
    private var ttsVoice: UniSoundTtsVoice = UniSoundTtsVoice.KIYO_PLUS

    override fun initTts(context: Context): UniSoundTtsHelper {
        uniSoundTtsEngine = UnisoundTtsEngine.Builder()
            .setAppKey(BuildConfig.UNI_SOUND_ASR_TTS_APP_KEY)
            .setAppSecret(BuildConfig.UNI_SOUND_ASR_TTS_SECRET_KEY)
            .setDeviceUUID(BuildConfig.UNI_SOUND_DEVICE_ID)
            .build(context)

        uniSoundTtsEngine.apply {
            setTtsOption(TtsOption.TTS_OPTION_PRINT_JNI_LOG, true)
            setTtsOption(TtsOption.TTS_OPTION_INIT_TR_ADDRESS, "poc-ai-tr-ws.hivoice.cn:443")
            setTtsOption(TtsOption.TTS_OPTION_VOICE_NAME, ttsVoice.name)
            //设置速度
            setTtsOption(TtsOption.TTS_OPTION_SPEED, 50)
            //设置音量
            setTtsOption(TtsOption.TTS_OPTION_VOLUME, 50)
            //设置声音亮度
            setTtsOption(TtsOption.TTS_OPTION_BRIGHT, 50)
            //设置音高
            setTtsOption(TtsOption.TTS_OPTION_PITCH, 50)
            //设置采样率
            setTtsOption(TtsOption.TTS_OPTION_SAMPLE_RATE, 16000)
            //设计监听
            setTtsEventListener(this@UniSoundTtsHelper)
            //在线模式
            changeEngineMode(UnisoundTtsMode.ONLINE)
            init()
        }
        return this
    }

    override fun onTtsEnd() {
        super.onTtsEnd()
        if (ttsQueue.isNotEmpty()){
            ttsQueue.poll()?.let { playTts(it) }
        }
    }

    override fun playTts(text: String) {
        uniSoundTtsEngine.playTts(text)
    }

    override fun playChunkTts(appendText: String) {
        Log.e(TAG, "playChunkTts: appendText= $appendText")
        _ttsBuffer.append(appendText)
        val matchResult = sentenceEndRegex.find(_ttsBuffer)
        if (matchResult != null) {
            // 取出完整的句子
            val sentence = matchResult.value.trim()
            _ttsBuffer.delete(0, matchResult.range.last + 1)
            Log.e(TAG, "playChunkTts: isPlaying= $isPlaying")
            if (isPlaying){
                ttsQueue.add(sentence)
                return
            }
            uniSoundTtsEngine.playTts(sentence, TtsPlayOption())
        }
    }

    fun playBuffer(buffer: ByteArray) {
        uniSoundTtsEngine.playBuffer(buffer, TtsPlayOption())
    }

    override fun pauseTts() {
        uniSoundTtsEngine.pause()
    }

    override fun resumeTts() {
        uniSoundTtsEngine.resume()
    }

    override fun cancelTts() {
        uniSoundTtsEngine.stop()
        ttsQueue.clear()
        _ttsBuffer.setLength(0)
    }

    override fun stopTts(immediately: Boolean) {
        uniSoundTtsEngine.stop()
        ttsQueue.clear()
        _ttsBuffer.setLength(0)
    }

    override fun releaseTts() {
        uniSoundTtsEngine.release()
        ttsQueue.clear()
        _ttsBuffer.setLength(0)
    }

    override fun onEvent(event: Int, msg: String?) {
        Log.d(TAG, "onEvent: $event $msg")
        when (event) {
            TtsEvent.TTS_EVENT_PLAY_START -> {
                onTtsStart()
            }

            TtsEvent.TTS_EVENT_PLAY_END -> {
                onTtsEnd()
            }

            TtsEvent.TTS_EVENT_PAUSE -> {
                onTtsPause()
            }

            TtsEvent.TTS_EVENT_RESUME -> {
                onTtsResume()
            }

            TtsEvent.TTS_EVENT_STOP -> {
                onTtsStop()
            }

            TtsEvent.TTS_EVENT_CANCEL -> {
                onTtsCancel()
            }
        }
    }

    override fun onError(error: Int, msg: String?) {
        onTtsError(error, msg ?: "")
    }

    fun updateVoiceType(voice: UniSoundTtsVoice) {
        ttsVoice = voice
        uniSoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOICE_NAME, voice.name)
    }

    fun updateVoiceType(name: String) {
        name.takeIf { ttsVoice.name != it }?.let {
            uniSoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOICE_NAME, name)
        }
    }

    sealed class UniSoundTtsVoice(val name: String) {
        data object CHEN_YU_LLM : UniSoundTtsVoice("chenyu-llm")
        data object CHEN_YU_ORAL : UniSoundTtsVoice("chenyu-oral")
        data object CHEN_YU_DEPRESSED_ORAL : UniSoundTtsVoice("chenyu-depressed-oral")
        data object CHEN_YU_FAST_ORAL : UniSoundTtsVoice("chenyu-fast-oral")
        data object CHEN_YU_HAPPY_ORAL : UniSoundTtsVoice("chenyu-happy-oral")
        data object CHEN_YU_LOUDLY_ORAL : UniSoundTtsVoice("chenyu-loundly-oral")
        data object CHEN_YU_SLOW_ORAL : UniSoundTtsVoice("chenyu-slow-oral")
        data object CHEN_YU_WHISPER_ORAL : UniSoundTtsVoice("chenyu-whisper-oral")
        data object CHEN_YU_ANGRY_ORAL : UniSoundTtsVoice("chenyu-angry-oral")
        data object KIYO_PLUS : UniSoundTtsVoice("kiyo-plus")
    }

}