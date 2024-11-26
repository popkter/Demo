package com.popkter.voice_assistant.base

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import com.popkter.voice_assistant.VuiStatementHelper
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseTtsHelper : ITtsHelper {

    private val _isPlaying = AtomicBoolean(false)

    val isPlaying: Boolean
        get() {
            return _isPlaying.get()
        }

    open val TAG = "BaseTtsManager"

    private val onTtsStatusChangedListeners = HashSet<OnTtsStatusChanged>()

    @CallSuper
    internal open fun onTtsStart() {
        Log.i(TAG, "onTtsStart")
        _isPlaying.set(true)
        onTtsStatusChangedListeners.forEach { it.onStart() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_START)
    }

    @CallSuper
    internal open fun onTtsEnd() {
        Log.i(TAG, "onTtsEnd")
        _isPlaying.set(false)
        onTtsStatusChangedListeners.forEach { it.onEnd() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_END)
    }

    @CallSuper
    internal open fun onTtsPause() {
        Log.i(TAG, "onTtsPause")
        _isPlaying.set(false)
        onTtsStatusChangedListeners.forEach { it.onPause() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_PAUSE)
    }

    @CallSuper
    internal open fun onTtsResume() {
        Log.i(TAG, "onTtsResume")
        _isPlaying.set(true)
        onTtsStatusChangedListeners.forEach { it.onResume() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_RESUME)
    }

    @CallSuper
    internal open fun onTtsCancel() {
        Log.d(TAG, "onTtsCancel: ")
        _isPlaying.set(false)
        onTtsStatusChangedListeners.forEach { it.onCancel() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_CANCEL)
    }

    @CallSuper
    internal open fun onTtsStop() {
        Log.i(TAG, "onTtsStop")
        _isPlaying.set(false)
        onTtsStatusChangedListeners.forEach { it.onStop() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.TTS_STOP)
    }

    @CallSuper
    internal open fun onTtsError(code: Int, message: String) {
        Log.i(TAG, "onTtsError $code $message")
        _isPlaying.set(false)
        onTtsStatusChangedListeners.forEach { it.onError(code, message) }
        VuiStatementHelper.onVuiStateChanged(
            VuiStatementHelper.VuiStatement.TTS_ERROR,
            errorCode = code,
            errorMsg = message
        )
    }

    fun registerTtsStatusListener(
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {},
        onError: (Int, String) -> Unit = { _, _ -> },
        onPause: () -> Unit = {},
        onResume: () -> Unit = {},
        onCancel: () -> Unit = {},
        onStop: () -> Unit = {}
    ) {
        onTtsStatusChangedListeners.add(
            OnTtsStatusChanged(onStart, onEnd, onError, onPause, onResume, onCancel, onStop)
        )
    }

    fun registerTtsStatusListener(onTtsStatusChanged: OnTtsStatusChanged) {
        onTtsStatusChangedListeners.add(onTtsStatusChanged)
    }

    fun unregisterTtsStatusListener(onTtsStatusChanged: OnTtsStatusChanged? = null) {
        onTtsStatusChanged?.run { onTtsStatusChangedListeners.remove(this) }
            ?: onTtsStatusChangedListeners.clear()
    }

    data class OnTtsStatusChanged(
        val onStart: () -> Unit = {},
        val onEnd: () -> Unit = {},
        val onError: (Int, String) -> Unit = { _, _ -> },
        val onPause: () -> Unit = {},
        val onResume: () -> Unit = {},
        val onCancel: () -> Unit = {},
        val onStop: () -> Unit = {}
    )

}

interface ITtsHelper {
    fun initTts(context: Context): BaseTtsHelper

    fun playTts(text: String)

    fun playChunkTts(appendText: String)

    fun pauseTts()

    fun resumeTts()

    fun cancelTts()

    fun stopTts(immediately: Boolean)

    fun releaseTts()
}