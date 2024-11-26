package com.popkter.voice_assistant.base

import android.content.Context
import androidx.annotation.CallSuper
import com.popkter.voice_assistant.VuiStatementHelper

abstract class BaseWakeUpHelper : IWakeUpHelper {

    open val TAG = "BaseWakeUpHelper"

    private val onWakeUpStatusChangedListeners = HashSet<OnWakeUpStatusChanged>()

    @CallSuper
    internal open fun onWakeUp() {
        onWakeUpStatusChangedListeners.forEach { it.onWakeUp() }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.WAKE_UP)
    }

    @CallSuper
    internal open fun onWakeUpError(code: Int, message: String?) {
        onWakeUpStatusChangedListeners.forEach { it.onError(code, message ?: "") }
        VuiStatementHelper.onVuiStateChanged(VuiStatementHelper.VuiStatement.WAKE_UP_ERROR)
    }


    fun registerWakeupStatusListener(onWakeUp: () -> Unit = {}, onError: (Int, String) -> Unit = { _, _ -> }) {
        onWakeUpStatusChangedListeners.add(OnWakeUpStatusChanged(onWakeUp, onError))
    }

    fun registerWakeupStatusListener(onWakeUpStatusChanged: OnWakeUpStatusChanged) {
        onWakeUpStatusChangedListeners.add(onWakeUpStatusChanged)
    }

    fun unregisterWakeupStatusListener(onWakeUpStatusChanged: OnWakeUpStatusChanged? = null) {
        onWakeUpStatusChanged?.run { onWakeUpStatusChangedListeners.remove(this) }
            ?: onWakeUpStatusChangedListeners.clear()
    }


    data class OnWakeUpStatusChanged(
        val onWakeUp: () -> Unit = {},
        val onError: (Int, String) -> Unit = { _, _ -> }
    )

}

interface IWakeUpHelper {
    fun initWakeUp(context: Context): BaseWakeUpHelper

    fun startWakeUp()

    fun stopWakeUp()

    fun releaseWakeUp()

    fun addWakeupWord(vararg keyWord: String)
}
