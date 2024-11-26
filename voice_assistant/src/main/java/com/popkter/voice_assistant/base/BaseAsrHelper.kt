package com.popkter.voice_assistant.base

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import com.popkter.voice_assistant.VuiStatementHelper

abstract class BaseAsrHelper : IAsrHelper {

    open val TAG = "BaseAsrManager"

    var duplexTimeout: DuplexTimeout = DuplexTimeout.ZERO
        private set

    private val onAsrStatusChangedListeners = HashSet<OnAsrStatusChanged>()

    @CallSuper
    open fun toggleFullDuplex(timeOut: DuplexTimeout) {
        Log.i(TAG, "toggleFullDuplex: timeout: ${timeOut.timeout} duplexTimeout: ${duplexTimeout.timeout}")
        if (duplexTimeout.timeout != timeOut.timeout) {
            duplexTimeout = timeOut
        }
    }

    open fun uploadHotWords(string: String) {}

    open fun uploadHotWordsByAssets(context: Context, assetsFileName: String) {}


    @CallSuper
    internal open fun onAsrStart() {
        onAsrStatusChangedListeners.forEach { it.onStart() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.ASR_START)
    }

    @CallSuper
    internal open fun onAsrResult(text: String) {
        onAsrStatusChangedListeners.forEach { it.onResult(text) }
        VuiStatementHelper.onVuiStateChanged(
            VuiStatementHelper.VuiStatement.ASR_RESULT,
            asrText = text
        )
    }

    @CallSuper
    internal open fun onAsrProcessing(text: String) {
        onAsrStatusChangedListeners.forEach { it.onProcessing(text) }
        VuiStatementHelper.onVuiStateChanged(
            VuiStatementHelper.VuiStatement.ASR_PROCESSING,
            asrText = text
        )
    }

    @CallSuper
    internal open fun onAsrError(code: Int, message: String) {
        onAsrStatusChangedListeners.forEach { it.onError(code, message) }
        VuiStatementHelper.onVuiStateChanged(
            VuiStatementHelper.VuiStatement.ASR_ERROR,
            errorCode = code,
            errorMsg = message
        )
    }

    fun registerAsrStatusListener(
        onStart: () -> Unit = {},
        onProcessing: (String) -> Unit = {},
        onResult: (String) -> Unit = {},
        onError: (Int, String) -> Unit = { _, _ -> }
    ) {
        onAsrStatusChangedListeners.add(
            OnAsrStatusChanged(onStart, onProcessing, onResult, onError)
        )
    }

    fun registerAsrStatusListener(onAsrStatusChanged: OnAsrStatusChanged) {
        onAsrStatusChangedListeners.add(onAsrStatusChanged)
    }

    fun unregisterAsrStatusListener(onAsrStatusChanged: OnAsrStatusChanged? = null) {
        onAsrStatusChanged?.run { onAsrStatusChangedListeners.remove(this) }
            ?: onAsrStatusChangedListeners.clear()
    }

    sealed class DuplexTimeout(val timeout: Long) {
        data object ZERO : DuplexTimeout(0L)
        data object HALF_ONE_MINUTE : DuplexTimeout(30000L)
        data object ONE_MINUTE : DuplexTimeout(60000L)
    }

    data class OnAsrStatusChanged(
        val onStart: () -> Unit = {},
        val onProcessing: (String) -> Unit = {},
        val onResult: (String) -> Unit = {},
        val onError: (Int, String) -> Unit = { _, _ -> }
    )
}

interface IAsrHelper {
    fun initAsr(context: Context): BaseAsrHelper

    fun startAsr()

    fun stopAsr(from: String)

    fun cancelAsr()

    fun releaseAsr()
}