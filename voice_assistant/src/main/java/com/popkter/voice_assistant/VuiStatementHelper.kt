package com.popkter.voice_assistant

import android.util.Log

object VuiStatementHelper {

    private const val TAG = "VuiStatementHelper"

    private val vuiStatementListeners = HashSet<VuiStatementListener>()

    internal fun onVuiStateChanged(
        state: VuiStatement,
        asrText: String = "",
        errorCode: Int = 0,
        errorMsg: String = ""
    ) {
        Log.e(TAG, "onVuiStateChanged: $state listener count: ${vuiStatementListeners.size}")
        vuiStatementListeners.forEach {
            when (state) {
                VuiStatement.ASR_START -> {
                    it.onAsrStart()
                }

                VuiStatement.ASR_PROCESSING -> {
                    it.onAsrProcessing(asrText)
                }

                VuiStatement.ASR_RESULT -> {
                    it.onAsrResult(asrText)
                }

                VuiStatement.ASR_ERROR -> {
                    it.onAsrError(errorCode, errorMsg)
                }

                VuiStatement.TTS_START -> {
                    it.onTtsStart()
                }

                VuiStatement.TTS_END -> {
                    it.onTtsEnd()
                }

                VuiStatement.TTS_PAUSE -> {
                    it.onTtsPause()
                }

                VuiStatement.TTS_RESUME -> {
                    it.onTtsResume()
                }

                VuiStatement.TTS_CANCEL -> {
                    it.onTtsCancel()
                }

                VuiStatement.TTS_STOP -> {
                    it.onTtsStop()
                }

                VuiStatement.TTS_ERROR -> {
                    it.onTtsError(errorCode, errorMsg)
                }

                VuiStatement.WAKE_UP -> {
                    Log.e(TAG, "onVuiStateChanged: onWakeUp")
                    it.onWakeUp()
                }

                VuiStatement.WAKE_UP_ERROR -> {
                    it.onWakeUpError(errorCode, errorMsg)
                }
            }
        }

    }


    fun registerVuiStatementListeners(listener: VuiStatementListener) {
        vuiStatementListeners.add(listener)
    }

    fun unregisterVuiStatementListeners(listener: VuiStatementListener? = null) {
        listener?.run {
            vuiStatementListeners.remove(this)
        } ?: vuiStatementListeners.clear()
    }


    fun registerVuiStatementListeners(
        onAsrStart: () -> Unit = {},
        onAsrProcessing: (text: String) -> Unit = {},
        onAsrResult: (text: String) -> Unit = {},
        onAsrError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },
        onTtsStart: () -> Unit = {},
        onTtsEnd: () -> Unit = {},
        onTtsError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },
        onWakeUp: () -> Unit = {},
        onWakeUpError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },
    ) {
        registerVuiStatementListeners(
            VuiStatementListener(
                onAsrStart = onAsrStart,
                onAsrProcessing = onAsrProcessing,
                onAsrResult = onAsrResult,
                onAsrError = onAsrError,
                onTtsStart = onTtsStart,
                onTtsEnd = onTtsEnd,
                onTtsError = onTtsError,
                onWakeUp = onWakeUp,
                onWakeUpError = onWakeUpError
            )
        )
    }

    /**
     * 状态监听
     */
    data class VuiStatementListener(
        val onAsrStart: () -> Unit = {},

        val onAsrProcessing: (text: String) -> Unit = {},

        val onAsrResult: (text: String) -> Unit = {},

        val onAsrError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },

        val onTtsStart: () -> Unit = {},

        val onTtsEnd: () -> Unit = {},

        val onTtsPause: () -> Unit = {},

        val onTtsResume: () -> Unit = {},

        val onTtsCancel: () -> Unit = {},

        val onTtsStop: () -> Unit = {},

        val onTtsError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },

        val onWakeUp: () -> Unit = {},

        val onWakeUpError: (errorCode: Int, errorMsg: String) -> Unit = { code, msg -> },
    )

    /**
     * 状态
     */
    sealed class VuiStatement {
        data object ASR_START : VuiStatement()
        data object ASR_PROCESSING : VuiStatement()
        data object ASR_RESULT : VuiStatement()
        data object ASR_ERROR : VuiStatement()
        data object TTS_START : VuiStatement()
        data object TTS_END : VuiStatement()
        data object TTS_PAUSE : VuiStatement()
        data object TTS_RESUME : VuiStatement()
        data object TTS_STOP : VuiStatement()
        data object TTS_CANCEL : VuiStatement()
        data object TTS_ERROR : VuiStatement()
        data object WAKE_UP : VuiStatement()
        data object WAKE_UP_ERROR : VuiStatement()
    }
}