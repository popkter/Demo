package com.senseauto.lib_tts

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.annotation.StringDef

const val SHA_SHA = "shasha"

const val CHENG_CHENG = "chengcheng"

@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressLint("UniqueConstants")
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    value = [
        SHA_SHA,
        CHENG_CHENG,
    ]
)
annotation class VoiceName
