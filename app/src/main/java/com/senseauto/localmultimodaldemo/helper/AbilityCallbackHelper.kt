package com.senseauto.localmultimodaldemo.helper

import com.senseauto.lib_sound.ability.AbilityCallback

class AbilityCallbackHelper(
    val onBegin: () -> Unit = {},
    val onEnd: () -> Unit = {},
    val onError: (code: Int, error: Throwable?) -> Unit = { _, _ -> },
    val onResult: (result: String) -> Unit = {},
) : AbilityCallback {
    override fun onAbilityBegin() {
        onBegin()
    }

    override fun onAbilityResult(result: String) {
        onResult(result)
    }

    override fun onAbilityError(code: Int, error: Throwable?) {
        onError(code, error)
    }

    override fun onAbilityEnd() {
        onEnd()
    }

}