package com.senseauto.lib_tts

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.unisound.active.AICodeType
import com.unisound.active.Config
import com.unisound.active.IActiveListener
import com.unisound.active.SDKActive
import com.unisound.offline.tts.ITtsEventListener
import com.unisound.offline.tts.TtsEvent
import com.unisound.offline.tts.TtsOption
import com.unisound.offline.tts.UnisoundOfflineTtsEngine
import com.unisound.utils.AssetsUtils
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.atomic.AtomicBoolean

class UniSoundTtsManager(private val context: Context) {
    private val TAG = "UniSoundTtsManager"
    private lateinit var ttsPath: String
    private lateinit var unisoundTtsEngine: UnisoundOfflineTtsEngine
    private val textQueue: Queue<TTSEntry> by lazy { LinkedList() }

    private val lock = Object()
    val isPlaying = AtomicBoolean(false)
    private var currentVnc = "shasha"
    private var root = ""


    fun initEngines() {

        val dir = context.getExternalFilesDir("unisound")
        root = dir?.absolutePath ?: context.cacheDir.absolutePath
        //基本模型拷贝
        AssetsUtils.copyDirsToPath(
            context,
            "ttsmodels",
            root + "/ttsmodels"
        )

        ttsPath = context.getExternalFilesDir("tts")?.absolutePath!!
        val speed = 60
     /*   if (SystemUtils.getSystemLanguage() == "en") {
            speed = 100
        }*/
        unisoundTtsEngine = UnisoundOfflineTtsEngine.Builder()
            .setSpeed(speed)
            .setTtsOption(TtsOption.TTS_OPTION_SAVE_TTS_PATH, ttsPath)
            .setTtsOption(TtsOption.TTS_OPTION_SAVE_TTS, false)
            //如果资源文件没有在aar中，则需要外部设置，不同用户不同使用方式，推荐外部设置这样代码量小
            .setTtsOption(
                TtsOption.TTS_OPTION_BACK_MODEL_PATH,
                "$root/ttsmodels/backend_model_cn_offline_shasha"
            )
            .setTtsOption(
                TtsOption.TTS_OPTION_FRONT_MODEL_PATH,
                "$root/ttsmodels/frontend_model_cn_offline"
            )
            .setTtsOption(
                TtsOption.TTS_OPTION_SAVE_TTS_TYPE,
                TtsOption.TtsFileType.WAV
            )
            .build(context)
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_PRINT_JNI_LOG, true)
        unisoundTtsEngine.addTtsEventListener(object : ITtsEventListener {
            override fun onEvent(event: Int, msg: String?) {
                when (event) {
                    TtsEvent.TTS_EVENT_INIT_SUCCESS -> {
                        Log.i(TAG, "tts init success")
                    }

                    TtsEvent.TTS_EVENT_ANALYZE_START -> {
                        Log.e(TAG, "开始生产")
                    }

                    TtsEvent.TTS_EVENT_ANALYZE_END -> {
                        Log.e(TAG, "结束生产")
                    }

                    TtsEvent.TTS_EVENT_PAUSE -> {
                        Log.e(TAG, "暂停播放")
                    }

                    TtsEvent.TTS_EVENT_RESUME -> {
                        Log.e(TAG, "继续播放")
                    }

                    TtsEvent.TTS_EVENT_PLAY_START -> {
                        Log.e(TAG, "开始播放")
                    }

                    TtsEvent.TTS_EVENT_PLAY_END -> {
                        Log.e(TAG, "结束播放")
                        isPlaying.set(false)
                        synchronized(lock) {
                            if (!textQueue.isEmpty()) {
                                if (!isPlaying.get()) {
                                    Log.i(TAG, "speak end, textQueue size: ${textQueue.size}")
                                    val item = textQueue.remove()
                                    changeVoice(item.vcnType)
                                    unisoundTtsEngine.playTts(item.text)
                                    isPlaying.set(true)
                                }
                            }
                        }
                    }

                    TtsEvent.TTS_EVENT_CHANGE_VOICE_SUCCESS -> {
                        Log.e(TAG, "切换成功了")
                    }
                }
            }

            override fun onError(error: Int, msg: String?) {
                Log.e(TAG, "onError:${error},msg:${msg}")
            }
        })
        unisoundTtsEngine.init()
    }

    fun playTts(text: String) {
        synchronized(this) {
            isPlaying.set(true)
            unisoundTtsEngine.playTts(text)
        }
    }

    fun playTts(entry: TTSEntry) {
        synchronized(lock) {
            if (!isPlaying.get()) {
                entry.vcnType
                isPlaying.set(true)
                changeVoice(entry.vcnType)
                unisoundTtsEngine.playTts(entry.text)
            } else {
                textQueue.add(entry)
            }
        }
    }

    fun changeVoice(vcn: String) {
        if (vcn == currentVnc) {
            return
        }
        if (voiceFileMap.containsKey(vcn)) {
            currentVnc = vcn
            unisoundTtsEngine.reloadBackendFilePath(root + voiceFileMap[vcn])
        }
    }

    fun release() {
        unisoundTtsEngine.release()
    }

    fun resume() {
        unisoundTtsEngine.resume()
    }

    fun stop() {
        unisoundTtsEngine.stop()
        synchronized(lock) {
            textQueue.clear()
            Log.i(TAG, "stop tts text queue size: ${textQueue.size}")
            isPlaying.set(false)
        }
    }

    private fun setUniSoundConfig(context: Context) {
        Config.setAppKey("jhahpsemw264ej63wwiyoqpyyka7rqt2hfxfytir")
        Config.setAppSecret("4abfec620f75b50f7776917f1839cea1")
        Config.setAiCode(AICodeType.AI_KWS_DICTATION_TTS_NLU_OFF)
        Config.setUdid("c8240625")
        Config.setLogEnabled(true)

        SDKActive.getInstance().setJniLoglevel(5)
        SDKActive.getInstance().active(context, object: IActiveListener {
            override fun success() {
                Log.i("setUniSoundConfig", "激活成功")
            }

            override fun onError(error: Int, msg: String?) {
                Log.i("setUniSoundConfig", "激活失败. msg: $msg")
                Toast.makeText(context, "错误信息：${msg}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    init {
        setUniSoundConfig(context)
    }

    companion object {
        private val voiceName = arrayOf<String?>(
            "shasha", "wangdi", "xiaoqin", "xuanxuan", "mingyu", "tiantian", "chengcheng", "chenyang"
        )

        private val voiceFileMap = mapOf(
            "chengcheng" to "/ttsmodels/backend_model_cn_offline_chengcheng",
            "chenyang" to "/ttsmodels/backend_model_cn_offline_chenyang",
            "mingyu" to "/ttsmodels/backend_model_cn_offline_mingyu",
            "shasha" to "/ttsmodels/backend_model_cn_offline_shasha",
            "tiantian" to "/ttsmodels/backend_model_cn_offline_tiantian",
            "wangdi" to "/ttsmodels/backend_model_cn_offline_wangdi",
            "xiaoqin" to "/ttsmodels/backend_model_cn_offline_xiaoqin",
            "xuanxuan" to "/ttsmodels/backend_model_cn_offline_xuanxuan"

        )
    }
 }