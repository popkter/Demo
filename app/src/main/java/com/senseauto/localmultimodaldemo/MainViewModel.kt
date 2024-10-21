package com.senseauto.localmultimodaldemo

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseauto.lib_sound.ability.ed.EdEnCnHelper
import com.senseauto.lib_tts.CHENG_CHENG
import com.senseauto.lib_tts.SHA_SHA
import com.senseauto.lib_tts.TTSEntry
import com.senseauto.lib_tts.UniSoundTtsManager
import com.senseauto.libcommon.appExt.ApplicationModule
import com.senseauto.localmultimodaldemo.constant.ListData
import com.senseauto.localmultimodaldemo.entity.HintItem
import com.senseauto.localmultimodaldemo.helper.AbilityCallbackHelper
import com.senseauto.localmultimodaldemo.helper.RecorderCallbackHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    companion object{
        const val TAG = "MainViewModel"
    }

    private val abilityCallback = AbilityCallbackHelper{ queryFlow.value = it }

    private val recorderCallback = RecorderCallbackHelper()

    private val ttsManager by lazy { UniSoundTtsManager(ApplicationModule.application).apply { initEngines() } }

    private val edEnCnHelper by lazy {
        EdEnCnHelper(callBack = abilityCallback).apply {
            setRecorderCallback(recorderCallback)
            loadCustomParams()
        }
    }

    private val chatRecordList = mutableListOf<ChatRecord>()
    private val chatRecordFlow = MutableSharedFlow<List<ChatRecord>>()
    val chatRecords = chatRecordFlow.asSharedFlow()

    private val takePhotoActionFlow = MutableStateFlow(false)
    val takePhotoAction = takePhotoActionFlow.asStateFlow()

    private val bitmapFlow = MutableStateFlow<Bitmap?>(null)
    private val queryFlow = MutableStateFlow("")
    private val isRecognizingFlow = MutableStateFlow(false)
    val isRecognizing = isRecognizingFlow.asStateFlow()
    val voiceInputResult = queryFlow.asStateFlow()

    private val tabIndexFlow = MutableStateFlow(0)
    val tabIndex = tabIndexFlow.asStateFlow()

    private val currentResultFlow = MutableStateFlow("" to "")
    val currentResult = currentResultFlow.asStateFlow()

    private var loadResultJob: Job? = null

    init {
        viewModelScope.launch {
            combine(bitmapFlow, queryFlow, isRecognizingFlow) { bitmap, query, recognizing ->
                if (bitmap != null && query.isNotEmpty() && !recognizing) {
                    loadResult(HintItem(query, query), bitmap)
                    bitmapFlow.value = null
                    queryFlow.value = ""
                }
            }.collect()
        }
    }

    fun startRecognize() {
        Log.e(TAG, "startRecognize: ")
        ttsManager.stop()
        isRecognizingFlow.value = true
        edEnCnHelper.switchAsr(true, isSpeechLongPress = true)
    }

    fun stopRecognize() {
        Log.e(TAG, "stopRecognize: ")
        isRecognizingFlow.value = false
        edEnCnHelper.switchAsr(false)
//        queryFlow.value = ""
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

    fun clearChatRecord(){
        viewModelScope.launch {
            loadResultJob?.cancel()
            chatRecordList.clear()
            chatRecordFlow.emit(emptyList())
        }
    }

    fun playTts(string: String, voiceName: String = SHA_SHA) {
        ttsManager.stop()
        ttsManager.playTts(TTSEntry(string, voiceName))
    }

    private var curIndex = -1
    private fun playAddedText(oldText: String, newText: String) {
        Log.e(TAG, "playAddedText: newText= $newText oldText= $oldText", )
        val addWord = newText.substring(oldText.length)
        val ind = addWord.indexOfLast {
            return@indexOfLast it == '，' || it == ',' || it == '.' || it == '。' || it == '?' || it =='？' || it == '!' || it == '！'
        }

        if (ind != -1) {
            // 防止越界错误
            if ((curIndex + 1 > newText.length) || (oldText.length + ind > newText.length)) {
                curIndex = -1
                ttsManager.stop()
                return
            }

            // 提取需要播放的追加文字
            val speakWord = newText.substring(curIndex + 1, oldText.length + ind)

            if (!isRecognizing.value) {
                // 只保留本地 TTS 播放逻辑
                ttsManager.playTts(TTSEntry(speakWord, "chengcheng"))
            }

            // 更新当前播放进度
            curIndex = oldText.length + ind
        }
    }

    fun loadResult(hintItem: HintItem, bitmap: Bitmap) {
        loadResultJob?.cancel()
        loadResultJob = viewModelScope.launch {
            chatRecordList.add(
                ChatRecord(
                    owner = Human,
                    tts = hintItem.hint,
                    image = bitmap
                )
            )
            chatRecordFlow.emit(chatRecordList.toList())

            // TODO: 加载模型回复

            val record = ChatRecord(owner = Bot, tts = "")
            chatRecordList.add(record)
            chatRecordFlow.emit(chatRecordList.toList())

            val demoResult = listOf(
                "今天的天气是十分美好","的，阳光明媚",", 万里无云，","天高云淡，海阔天空。"
            )

            coroutineScope {
                demoResult.forEach {
                    if (loadResultJob?.isActive == true) {
                        playAddedText(record.tts, record.tts + it)
                        record.tts += it
                        currentResultFlow.emit(record.id to record.tts)
                        delay(100)
                    }
                }
            }
        }
    }

    fun updateTabIndex(index: Int) {
        if (index != tabIndex.value){
            tabIndexFlow.value = index
            playTts(ListData[index].title, CHENG_CHENG)
            clearChatRecord()
        }
    }

}


data class ChatRecord(
    val owner: ChatOwnerType,
    var tts: String,
    val image: Bitmap? = null,
    val imageUrl: String? = null,
    val imageBase64: String? = null,
    val id: String = UUID.randomUUID().toString()
)

sealed class ChatOwnerType()
data object Bot : ChatOwnerType()
data object Human : ChatOwnerType()