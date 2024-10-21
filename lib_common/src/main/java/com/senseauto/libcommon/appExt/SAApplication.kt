package com.senseauto.libcommon.appExt

import android.app.Application
import androidx.annotation.CallSuper
import com.tencent.mmkv.MMKV

/**
 * @desc  : Application超类
 * @author: Pop
 * @since  : 2024/10/8 19:28
 */
open class SAApplication : Application() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        ApplicationModule.init(this)
        MMKV.initialize(this)
    }

}