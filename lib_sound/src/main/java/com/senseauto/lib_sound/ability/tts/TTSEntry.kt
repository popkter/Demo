package com.senseauto.lib_sound.ability.tts

import com.senseauto.lib_sound.ability.AbilityCallback

class TTSEntry {
    private var text: String? = null
    private var vcnType: String? = null
    private var mCallback: AbilityCallback? = null

    // 构造函数，用于初始化对象的属性
    fun setTTSEntry(text: String?, type: String?) {
        this.text = text
        this.vcnType = type
    }

    // 获取文本字符的方法
    fun getText(): String? {
        return text
    }

    // 获取类型的方法
    fun getType(): String? {
        return vcnType
    }

    fun getCallback(): AbilityCallback? {
        return mCallback
    }
}