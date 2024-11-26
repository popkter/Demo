package com.popkter.voice_assistant.impl.unisound.asr

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.popkter.voice_assistant.BuildConfig
import com.popkter.voice_assistant.base.BaseAsrHelper
import com.unisound.sdk.asr.AsrEvent
import com.unisound.sdk.asr.AsrOption
import com.unisound.sdk.asr.UnisoundAsrEngine
import com.unisound.sdk.asr.impl.IAsrResultListener
import com.unisound.sdk.asr.param.UnisoundAsrInitMode
import com.unisound.upload.OnlineUploadHots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class UniSoundAsrHelper : IAsrResultListener, CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()), BaseAsrHelper() {

    private var duplexTimeoutJob: Job? = null
    private val gson = Gson()
    private lateinit var uniSoundAsrEngine: UnisoundAsrEngine

    private val hotWordsManager by lazy {
        OnlineUploadHots().apply {
            setOnUploadResultListener(object : OnlineUploadHots.OnUploadResultListener {
                override fun onDelete(success: Boolean, data: MutableMap<String, MutableList<String>>?) {
                    Log.e(TAG, "onDelete: $success")
                }

                override fun onUpload(success: Boolean, data: MutableMap<String, MutableList<String>>?) {
                    Log.e(TAG, "onUpload: $success")
                }

                override fun onGet(success: Boolean, data: MutableMap<String, MutableList<String>>?) {
                    Log.e(TAG, "onGet: $success")
                }

                override fun onAdd(success: Boolean, data: MutableMap<String, MutableList<String>>?) {
                    Log.e(TAG, "onAdd: $success")
                }

            })
        }
    }

    override val TAG: String
        get() = "AsrManager"

    companion object {
        val INSTANCE by lazy { UniSoundAsrHelper() }
    }

    override fun initAsr(context: Context): UniSoundAsrHelper {
        if (this::uniSoundAsrEngine.isInitialized) {
            uniSoundAsrEngine.stopAsr()
            uniSoundAsrEngine.release()
        }

        uniSoundAsrEngine = UnisoundAsrEngine(
            context,
            BuildConfig.UNI_SOUND_ASR_TTS_APP_KEY,
            BuildConfig.UNI_SOUND_ASR_TTS_SECRET_KEY,
            true
        )

        uniSoundAsrEngine.apply {
            //全双工
            setOption(AsrOption.ASR_OPTION_DUPLEX, true)
            //同步请求Nlu结果
            setOption(AsrOption.ASR_OPTION_NLU_ENABLE, false)
            //TR地址
            setOption(AsrOption.ASR_OPTION_TR_ADDRESS, "poc-ai-tr-ws.hivoice.cn:443")
            //打印 JNI LOG
            setOption(AsrOption.ASR_OPTION_PRINT_JNI_LOG, false)
            //设置是否返回数字识别结果
            setOption(AsrOption.ASR_OPTION_NUMBER_CONVERT, true)
            //设置自定义引擎参数
            setOption(AsrOption.ASR_OPTION_SELF_ENGINE_PARAMS, "vad_ts_num=1000")
            //设置自定义NLU参数 optional(protocolVersion=2.0)
            setOption(AsrOption.ASR_OPTION_SELF_NLU_PARAMS, "protocolVersion=2.0")
            //保存录音开关
            setOption(AsrOption.ASR_OPTION_SAVE_RECORD, false)
            //语义的城市信息
            setOption(AsrOption.ASR_OPTION_CITY, "上海市")
            //NLU 是否支持多语义
            setOption(AsrOption.ASR_OPTION_NLU_MULTI, true)
            //设置filterName
            setOption(AsrOption.ASR_OPTION_FILTER_NAME, "")
            //设置语义云服务协议版本号ver
            setOption(AsrOption.ASR_OPTION_NLU_VER, "3.2")
            //设置识别所属领域
            setOption(AsrOption.ASR_OPTION_ASR_DOMAIN, "general,poi,voice_box,incar_base,incar_extend")
            //设置语义识别场景
            setOption(AsrOption.ASR_OPTION_NLU_SCENARIO, "incar")
            //vad前端超时
            setOption(AsrOption.ASR_OPTION_VAD_FRONT_TIME, 5.0F)
            //vad后端超时
            setOption(AsrOption.ASR_OPTION_VAD_BACK_TIME, 0.5F)
            //声学模型
            setOption(AsrOption.ASR_OPTION_VOICE_FIELD, "md")
            //通用唯一识别码
            setOption(AsrOption.ASR_OPTION_DEVICE_UDID, BuildConfig.UNI_SOUND_DEVICE_ID)
            //流式交互版本，语义缩紧标志
            setOption(AsrOption.ASR_OPTION_OPEN_FULL_DUPLEX, false)

            setOption(AsrOption.ASR_OPTION_INIT_MODE, UnisoundAsrInitMode.ONLINE)

            onlineHots.setHost("casr.uat.hivoice.cn")

            setListener(this@UniSoundAsrHelper)

            init()
        }

        return this
    }

    override fun startAsr() {
        duplexTimeoutJob?.cancelChildren()
        duplexTimeoutJob = null
        uniSoundAsrEngine.startAsr()
    }

    override fun stopAsr(from: String) {
        Log.e(TAG, "stopAsr: $from")
        duplexTimeoutJob?.cancel()
        duplexTimeoutJob = null
        uniSoundAsrEngine.stopAsr()
    }

    fun stopAsr(stopRecord: Boolean = false, from: String = "") {
        Log.e(TAG, "stopAsr: $from")
        duplexTimeoutJob?.cancel()
        duplexTimeoutJob = null
        uniSoundAsrEngine.stopAsr()
    }

    override fun cancelAsr() {
        duplexTimeoutJob?.cancel()
        duplexTimeoutJob = null
        uniSoundAsrEngine.cancel()
    }

    override fun releaseAsr() {
        duplexTimeoutJob?.cancel()
        duplexTimeoutJob = null
        uniSoundAsrEngine.release()
    }

    override fun onResult(event: Int, jsonResult: String) {
        Log.d(TAG, "Received result: $jsonResult")
        when (event) {
            AsrEvent.ASR_EVENT_ASR_RESULT -> {
                runCatching {
                    val result = gson.fromJson(jsonResult, UniSoundAsrResultItem::class.java)
                    result.run {
                        if (vad) {
                            fixText?.run { onAsrResult(this) }
                            duplexTimeoutJob?.cancel()
                            duplexTimeoutJob = launch {
                                delay(duplexTimeout.timeout)
                                stopAsr("vad end")
                            }
                        } else {
                            duplexTimeoutJob?.cancelChildren()
                            varText?.run { onAsrProcessing(this) }
                        }
                    }

                }.onFailure {
                    Log.e(TAG, "onResult Error: ${it.message}")
                }
            }

            else -> {}
        }
    }

    override fun onEvent(event: Int, msg: String) {
        Log.d(TAG, "Event: $event, Msg: $msg")
        when (event) {
            AsrEvent.ASR_EVENT_RECORD_START -> {
                Log.d(TAG, "onEvent ASR_EVENT_ENTER_ASR")
                onAsrStart()
            }

            AsrEvent.ASR_EVENT_ENTER_IDLE -> Log.d(TAG, "onEvent ASR_EVENT_ENTER_IDLE")
            AsrEvent.ASR_EVENT_VAD_SPEECH_START -> Log.w(TAG, "onEvent ASR_EVENT_VAD_SPEECH_START")
            AsrEvent.ASR_EVENT_VAD_SPEECH_END -> Log.w(TAG, "onEvent ASR_EVENT_VAD_SPEECH_END")
        }
    }

    override fun onError(error: Int, errorMsg: String) {
        Log.e(TAG, "Error: $error, Msg: $errorMsg")
        onAsrError(error, errorMsg)
        duplexTimeoutJob?.cancel()
        duplexTimeoutJob = launch {
            delay(duplexTimeout.timeout)
            stopAsr("error")
        }
    }

    override fun onSessionId(sessionId: String) {
        Log.d(TAG, "Session ID: $sessionId")
    }

    override fun uploadHotWordsByAssets(context: Context, assetsFileName: String) {
        kotlin.runCatching {
            val jsonString =
                context.assets.open(assetsFileName).bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)

            val hotList = jsonObject.getJSONArray("hot").let { array ->
                List(array.length()) { index -> array.getString(index) }
            }

            val asrContactList = jsonObject.getJSONArray("asr_contact").let { array ->
                List(array.length()) { index -> array.getString(index) }
            }

            hotWordsManager.upload(mapOf("hot" to hotList, "asr_contact" to asrContactList))
        }
    }

    fun addHotWords(list: List<String>) {
        hotWordsManager.add(mapOf("hot" to list))
    }

    fun addContactHotWords(list: List<String>) {
        hotWordsManager.add(mapOf("asr_contact" to list))
    }



    data class UniSoundAsrResultItem(
        val ct: String,
        val fixText: String? = null,
        val varText: String? = null,
        val text: String,
        val sdk_result_type: String,
        val sessionID: String,
        val vad: Boolean
    )

}