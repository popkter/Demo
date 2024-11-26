package com.popkter.voice_assistant.impl.unisound.wake_up

import android.content.Context
import android.util.Log
import com.popkter.voice_assistant.BuildConfig
import com.popkter.voice_assistant.base.BaseWakeUpHelper
import com.unisound.active.AICodeType
import com.unisound.active.Config
import com.unisound.active.IActiveListener
import com.unisound.active.SDKActive
import com.unisound.kws.IKwsResultListener
import com.unisound.kws.KwsEvent
import com.unisound.kws.KwsOption
import com.unisound.kws.UnisoundKws
import com.unisound.kws.wakeup.WakeUpWord
import com.unisound.sdk.utils.AssetsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UniSoundWakeUpHelper : BaseWakeUpHelper(),
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()), IKwsResultListener {

    override val TAG: String
        get() = "UniSoundWakeUpHelper"

    companion object {
        val INSTANCE by lazy { UniSoundWakeUpHelper() }
    }

    private lateinit var uniSoundWakeEngine: UnisoundKws

    override fun initWakeUp(context: Context): UniSoundWakeUpHelper {

        launch {
            AssetsUtils.copyDirsToPath(context, "kwsmodels/wakeup", "${context.filesDir}/wakeup")
            AssetsUtils.copyDirsToPath(context, "kwsmodels/ammodel", "${context.filesDir}/ammodel")
            AssetsUtils.copyDirsToPath(context, "kwsmodels/compiler", "${context.filesDir}/compiler")

            Config.setAppKey(BuildConfig.UNI_SOUND_WAKE_UP_APP_KEY)
            Config.setAppSecret(BuildConfig.UNI_SOUND_WAKE_UP_SECRET_KEY)
            Config.setUdid(BuildConfig.UNI_SOUND_DEVICE_ID)
            Config.setAiCode(AICodeType.AI_KWS_DICTATION_TTS_NLU_OFF)
            Config.setLogEnabled(true)
            SDKActive.getInstance().active(context, object : IActiveListener {
                override fun success() {
                    uniSoundWakeEngine = UnisoundKws(context)
                    uniSoundWakeEngine.apply {
                        setOption(KwsOption.KWS_OPTION_INIT_GRAMMAR_PATH, "${context.filesDir}/wakeup/grammar.dat")
                        setOption(KwsOption.KWS_OPTION_INIT_AM_PATH, "${context.filesDir}/ammodel/lp_asrfix.dat")
                        setOption(KwsOption.KWS_OPTION_COMPILER_MODEL_PATH, "${context.filesDir}/compiler/asrfix.dat")
                        addListener(this@UniSoundWakeUpHelper)
                    }
                    uniSoundWakeEngine.init()
                    uniSoundWakeEngine.setWakeUpWord(setOf("小爱同学"))
                    uniSoundWakeEngine.startWakeUp()
                    Log.i(TAG, "active successful.")
                }

                override fun onError(error: Int, msg: String) {
                    Log.i(TAG, "active failed.")
                }
            })
        }
        return this
    }

    override fun startWakeUp() {
        uniSoundWakeEngine.cancel()
        uniSoundWakeEngine.startWakeUp(true)
    }

    override fun stopWakeUp() {
        uniSoundWakeEngine.cancel()
    }

    override fun releaseWakeUp() {
        uniSoundWakeEngine.release()
    }

    override fun addWakeupWord(vararg keyWord: String) {
        uniSoundWakeEngine.setWakeUpWord(keyWord.toSet().map {
            WakeUpWord(it, 0F)
        })
    }

    override fun onResult(event: Int, msg: String?) {
        if (event == KwsEvent.KWS_EVENT_RESULT) {
            Log.e(TAG, "KWS_EVENT_OFFLINE_RESULT:${event},result:${msg}")
        } else if (event == KwsEvent.KWS_EVENT_WAKEUP_RESULT) {
            Log.e(TAG, "KWS_EVENT_WAKEUP_RESULT:${event},result:${msg}")
            onWakeUp()
        }
    }

    override fun onEvent(event: Int, msg: String?) {
        when (event) {
            KwsEvent.KWS_EVENT_SET_WAKEUP_WORD_DONE -> {
                Log.e(TAG, "KWS_EVENT_SET_WAKEUP_WORD_DONE:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_SPEECH_VOLUME -> {
                Log.e(TAG, "KWS_EVENT_SPEECH_VOLUME:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_INIT_DONE -> {
                Log.e(TAG, "KWS_EVENT_OFFLINE_INIT_DONE:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_VAD_START -> {
                Log.e(TAG, "KWS_EVENT_OFFLINE_VAD_START:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_VAD_END -> {
                Log.e(TAG, "KWS_EVENT_OFFLINE_VAD_END:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_RECORD_START -> {
                Log.e(TAG, "KWS_EVENT_RECORD_START:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_RECORD_STOP -> {
                Log.e(TAG, "KWS_EVENT_RECORD_STOP:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_ENTER_IDLE -> {
                Log.e(TAG, "KWS_EVENT_ENTER_IDLE:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_ENTER_WAKE_UP -> {
                Log.e(TAG, "KWS_EVENT_ENTER_WAKE_UP:${event},msg:${msg}")
            }
            KwsEvent.KWS_EVENT_ENTER_ASR -> {
                Log.e(TAG, "KWS_EVENT_ENTER_ASR:${event},msg:${msg}")
            }
        }
    }

    override fun onError(event: Int, msg: String?) {
        Log.e(TAG, "onError: $event $msg")
        onError(event, msg)
    }

}