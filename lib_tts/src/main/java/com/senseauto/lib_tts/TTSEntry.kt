package com.senseauto.lib_tts

data class TTSEntry(
    val text: String? = null,
    @VoiceName val vcnType: String = SHA_SHA,
)