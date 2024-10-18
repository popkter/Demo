package com.senseauto.localmultimodaldemo

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseauto.localmultimodaldemo.entity.HintItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    companion object{
        const val TAG = "MainViewModel"
    }

    private val chatRecordList = mutableListOf<ChatRecord>()
    private val chatRecordFlow = MutableSharedFlow<List<ChatRecord>>()
    val chatRecords = chatRecordFlow.asSharedFlow()

    private val takePhotoActionFlow = MutableStateFlow(false)
    val takePhotoAction = takePhotoActionFlow.asStateFlow()

    private val bitmapFlow = MutableStateFlow<Bitmap?>(null)
    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(bitmapFlow, queryFlow) { bitmap, query ->
                if (bitmap != null && query.isNotEmpty()) {
                    loadResult(HintItem(query, query), bitmap)
                    bitmapFlow.value = null
                    queryFlow.value = ""
                }
            }.collect()
        }
    }

    fun startRecognize() {
        queryFlow.value = "语音识别结果测试${System.currentTimeMillis()}"
    }

    fun stopRecognize() {
        queryFlow.value = ""
    }

    fun stopTakePhoto() {
        takePhotoActionFlow.value = false
    }

    fun startTakePhoto() {
        takePhotoActionFlow.value = true
    }

    fun updateBitmap(bitmap: Bitmap) {
        Log.e(TAG, "updateBitmap: $bitmap")
        bitmapFlow.value = bitmap
    }

    fun updateQuery(query: String) {
        queryFlow.value = query
    }

    fun loadResult(hintItem: HintItem, bitmap: Bitmap) {
        viewModelScope.launch {
            chatRecordList.add(
                ChatRecord(
                    owner = Human,
                    tts = hintItem.hint,
                    image = bitmap
                )
            )
            chatRecordFlow.emit(chatRecordList.toList())

            // TODO: 加载模型回复

            chatRecordList.add(
                ChatRecord(
                    owner = Bot,
                    tts = "机器人回复"
                )
            )
            chatRecordFlow.emit(chatRecordList.toList())
        }
    }
}


data class ChatRecord(
    val owner: ChatOwnerType,
    val tts: String,
    val image: Bitmap? = null,
    val imageUrl: String? = null,
    val imageBase64: String? = null
)

sealed class ChatOwnerType()
data object Bot : ChatOwnerType()
data object Human : ChatOwnerType()