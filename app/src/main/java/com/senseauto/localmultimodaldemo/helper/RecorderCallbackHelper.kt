package com.senseauto.localmultimodaldemo.helper

import com.senseauto.lib_sound.media.audio.RecorderCallback
import java.io.File

class RecorderCallbackHelper(
    val onStart: () -> Unit = {},
    val onPause: () -> Unit = {},
    val onResume: () -> Unit = {},
    val onStop: (output: File?) -> Unit = {},
    val onProgress: (data: ByteArray, sampleSize: Int, volume: Int) -> Unit = { _, _, _ -> },
) : RecorderCallback {
    override fun onStartRecord() {
        onStart()
    }

    override fun onPauseRecord() {
        onPause()
    }

    override fun onResumeRecord() {
        onResume()
    }

    override fun onRecordProgress(data: ByteArray, sampleSize: Int, volume: Int) {
        onProgress(data, sampleSize, volume)
    }

    override fun onStopRecord(output: File?) {
        onStop(output)
    }
}