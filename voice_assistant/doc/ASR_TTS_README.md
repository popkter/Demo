# ProSdk

#### 介绍
Android的ASR和TTS的SDK，

#### 安装教程
1. 运行前需要动态获取录音权限用于ASR服务

```

   <uses-permission android:name="android.permission.RECORD_AUDIO"/>
   
```

#### 混淆说明
SDK内部做了混淆设置，业务层在做混淆的时候，不需要对其进行混淆。

```
-keep class cn.yunzhisheng.** { *; }
-keep class com.unisound.** { *; }

```

#### ASR接口说明

##### 1、主要的类UnisoundAsrEngine接口

* 构造函数

UnisoundAsrEngine(Context context)

* 构造函数
* 回调函数所在的线程：是工作线程还是主线程
UnisoundAsrEngine(Context context, boolean callBackInMainThread)

* 构造函数
* 设置appkey和secret
* 回调函数所在的线程：默认主线程
UnisoundAsrEngine(Context context,String appKey,String secret)

* 构造函数
* 设置appkey和secret
* 回调函数所在的线程：是工作线程还是主线程
UnisoundAsrEngine(Context context,String appKey,String secret, boolean callBackInMainThread)

* 构造函数
* 设置appkey和secret
* 音频源接口：可以设置@see FileAudioSource类的文件源，默认值：AndroidRecordAudioSource对象
* 回调函数所在的线程：是工作线程还是主线程
UnisoundAsrEngine(Context context, String appKey, String secret, IAudioSource iAudioSource,
      boolean callBackInMainThread)

* 初始化引擎
* @return true|false仅仅表示调用是否成功
boolean init()

* 设置音频输入
* 内部默认类：AndroidRecordAudioSource.
* 内部其他类：FileAudioSource 用于读取文件的，类通过文件PATH创建
*            DynamicAudioSource 用于直接设置音频内容的
void setAudioSource(IAudioSource audioSource)

* 设置结果回调的接口
void setListener(final IAsrResultListener iAsrResultListener)

* 设置参数
* AsrOption 类是具体设置的项目，后面有详细类型

void setOption(AsrOption key, Object value)

* 设置oneshot在线唤醒词
* @param wakeup 唤醒词集合
void setOneShotOnlineWakeupWord(List<String> wakeup)

* 取消识别，引擎进入{@link EngineState#Idle }状态
*
* @param closeRecord 是否停止录音
* @see com.unisound.sdk.asr.EngineState#Idle
void cancel()

* 开始识别，引擎进入{@link EngineState#Asr }状态
*
* @param oneShot 是否开启oneshot模式
boolean startAsr(boolean oneShot)

* 开始识别，引擎进入{@link EngineState#Asr }状态
*
* @param oneShot 默认为：false
boolean startAsr()

* 停止识别
*
* @param closeRecord boolean true 停止录音 /false 录音不停
* @return 成功true 失败false
boolean stopAsr(boolean closeRecord)

* 停止识别
*
* @param closeRecord 默认值：true
void stopAsr()

* 
* 释放资源
* 
void release()


##### 2、AsrOption 参数说明


* 设置是否打印 jni log
* 默认值：false

ASR_OPTION_PRINT_JNI_LOG,

* 设置打印日志的级别
* 默认值：1


ASR_OPTION_JNI_LOG_LEVEL,


* 设置发送音频的size大小
* 非必要不要设置
* 默认值：9600

ASR_OPTION_ONLINE_PACKAGE_SIZE,

* TR地址
* 默认值：ip:port

ASR_OPTION_TR_ADDRESS,

* ASR是否支持全双工

ASR_OPTION_DUPLEX,


* ASR DNS获取时间超时

ASR_OPTION_DNS_TIMEOUT,


* ASR 连接服务器的超时时间

ASR_OPTION_CONNECT_TIMEOUT,

* NLU 是否支持多语义

ASR_OPTION_NLU_MULTI,

* 声源定位角度信息

ASR_OPTION_DOA_RESULT,

* 通用唯一识别码

ASR_OPTION_DEVICE_UDID,

* 设置audioUrl开关
* 默认值： 由服务端配置

ASR_OPTION_OPEN_AUDIOURL,

* 设置流式交互版本，语义缩紧标志
* 默认值： false

ASR_OPTION_OPEN_FULL_DUPLEX,

* 设置语义识别场景
* 默认值：child
* 取值范围：｛
* "child", "incar", "smarthome", 
* "musicDefault", "videoDefault", 
* "webbrowser", "videoAPP"｝

ASR_OPTION_NLU_SCENARIO,

* 设置识别所属领域
* 默认值：general
* 取值范围：｛
* "kar", "song", "medical", "law", 
* "customized", "fridge", "aux", "poi", 
* "food", "general", "home", "movietv", 
* "incar", "ysdw", "home_md", "english", 
* "voice_box","hotel",
* "patientGuide",  "enrich", "eshopping"｝

ASR_OPTION_ASR_DOMAIN,


* 录音缓冲大小
* 默认值：1600

ASR_OPTION_RECORD_BUFFER_LENGTH,

* 设置语义云服务协议版本号ver
* 默认值：3.0

ASR_OPTION_NLU_VER,

* 设置是否打开可变结果
* 默认值：true

ASR_OPTION_TEMP_RESULT_ENABLE,



* 设置输送的音频格式
* 默认值："pcm-en","pcm"。
* 不建议设置，用默认值

ASR_OPTION_COMPRESS_FORMAT,


* 语音编码压缩率
* 默认值：8

ASR_OPTION_OPUS_COMPRESS_RATIO,

* 设置filterName
* 默认值：search
* 取值范围：｛search nlu3｝

ASR_OPTION_FILTER_NAME,

* 设置ASR特定功能URL

ASR_OPTION_FILTER_URL,

* 设置additionalService

ASR_OPTION_ADDITIONAL_SERVICE,

* 设置语义的GPS信息

ASR_OPTION_GPS,

* 设置语义的城市信息

ASR_OPTION_CITY,

* 设置保存录音开关

ASR_OPTION_SAVE_RECORD,

* 设置保存在线音频开关

ASR_OPTION_SAVE_ONLINE_AUDIO,

* 同步请求语义结果
* 默认值：true

ASR_OPTION_NLU_ENABLE,

* 设置语义的语言
* 默认值：cn

ASR_OPTION_LANGUAGE,

* 初始化模式
* 默认值：UnisoundAsrInitMode.MIX
* 取值范围：｛ONLINE｝

ASR_OPTION_INIT_MODE,

* 识别模式
* 默认值：UnisoundAsrMode.ONLINE
* 取值范围：｛ONLINE｝

ASR_OPTION_ASR_MODE,

* VAD 工作模式
* 默认值：VadWorkMode.ONLINE
* 取值范围：｛ONLINE, DISABLE｝

ASR_OPTION_VAD_MODE,

* 设置vad前端超时
* 默认值： 5.0f
* 单位：s

ASR_OPTION_VAD_FRONT_TIME,


* 设置Oneshot模式下vad前端超时,实际生效的值为这个超时时间和 ASR_OPTION_VAD_FRONT_TIME 取小
* 默认值： 0.2f
* 单位：s

ASR_OPTION_ONESHOT_VAD_FRONT_TIME,


* 设置vad后端超时
* 默认值： 0.4f
* 单位：s

ASR_OPTION_VAD_BACK_TIME,

* DEVICE_TOKEN

ASR_OPTION_DEVICE_TOKEN,

* 回调音量开关
* 默认值： false

ASR_OPTION_CALLBACK_VOLUME,

* 设置在线识别标点符号
* 默认值： false

ASR_OPTION_USE_PUNCTUATED,

* 设置标点符号标注的类型
* 默认值：default
* 取值范围：｛"beauty", "default"｝

ASR_OPTION_USE_PUNCTUATED_TYPE,

* 设置是否返回最高得分领域的最好结果
* 默认值： false

ASR_OPTION_BEST_RESULT_RETURN,

* 引擎端优化
* 不设置或设置为 0 时，即等同于旧的公有云
* 设置为非零值时，都将对识别率有负面影响，尤其是文本内容不受限的情境
* 默认值： 0
* 范围： 0,1,2,3,4

ASR_OPTION_FALSE_ALARM,

* 设置是否开启置信度功能
* 默认值： false

ASR_OPTION_CONFIDENCE_MEASURE,

* 置信度阈值
* 置信度大于threshold，则返回识别结果，否则不返回
* 默认值： 0.0f
* 范围：｛0 - 10.0｝

ASR_OPTION_CONFIDENCE_THRESHOLD,

* 设置是否返回数字识别结果
* 默认值： true

ASR_OPTION_NUMBER_CONVERT,


* 设置自定义NLU参数
* 默认值： ""

ASR_OPTION_SELF_NLU_PARAMS,

* 设置自定义引擎参数
* 默认值： ""

ASR_OPTION_SELF_ENGINE_PARAMS,

* 设置是否返回时间点
* 默认值： false

ASR_OPTION_RET_TIME_POINT,

* 设置声学模型
* 默认值：near
* 取值范围：｛"far", "near", "md"｝

ASR_OPTION_VOICE_FIELD,

* 设置domains_penalty的档位
* 默认值：0
* 取值范围：｛0-9｝

ASR_OPTION_DOMAINS_PENALTY,


* 设置oneShot版本号
* 1采用分割唤醒词的方法实现
* 2采用上传唤醒词的方法实现
* 默认值：2
* 取值范围：｛1-2｝

ASR_OPTION_ONE_SHOT_VERSION,

* 设置的oneshotKey。这个是在线使用的时候使用的

ASR_OPTION_ONESHOT_KEY,


* (oneshot1.0)在 oneshot=true 时生效
* 是否返回普通结果，如果为 false ，则只有语音以唤醒词开头时，才返回识别结果；
* 如果为 true，则无论语音是否以唤醒词开头，都返回识别结果
*   默认值: true

ASR_OPTION_RET_NORMAL_RESULT,


* (oneshot1.0)在 oneshot=true 时生效
* 是否在识别结果中返回唤醒词，
* 如果为 true 则返回结果的开头为唤醒词，
* 如果为 false 则不返回唤醒词

ASR_OPTION_RET_ONESHOT_KEY,

* 设置在线设置唤醒词的个数
* 是否必须设置：否
* 默认值：10
* 值范围：(0,10]

ASR_OPTION_MAX_ONLINE_WAKEUPWORDS_NUM,


* 在线识别用的adcode 值

ASR_OPTION_ADCODE,


* 支持https：

ASR_OPTION_HTTPS_ENABLED,

* 热词地址
* 默认值：casr.hivoice.cn

ASR_OPTION_UPLOAD_HOST_ADDRESS

##### 3、AsrEvent 参数说明

* 识别结果事件

ASR_EVENT_ASR_RESULT = 3202;

* 识别开始录音事件

ASR_EVENT_RECORD_START = 3203;

* 停止录音事件

ASR_EVENT_RECORD_STOP = 3204;

* 引擎状态事件-识别

ASR_EVENT_ENTER_ASR = 3207;

* 引擎状态事件-空闲

ASR_EVENT_ENTER_IDLE = 3208;

* 本地语音端点检测开始事件

ASR_EVENT_VAD_SPEECH_START = 3209;

* 本地语音端点检测结束事件

ASR_EVENT_VAD_SPEECH_END = 3210;

* 本地语音端点检测超时事件

ASR_EVENT_VAD_FRONT_TIME_OUT = 3211;


* 音量事件

ASR_EVENT_SPEECH_VOLUME = 3212;

* 录音文件读取结束

ASR_EVENT_RECORD_FILE_OVER = 3214;


* oneshot本地语音端点检测超时事件

ASR_EVENT_ONESHOT_VAD_TIME_OUT = 3215;


* ASR onEvent type
* 上传在线oneShot唤醒词成功

ASR_EVENT_UPLOAD_ONLINE_ONESHOT_WAKEUPWORD_SUCCESS = 1160;

##### 4、AsrError 参数说明

* 录音错误
ASR_ERROR_RECORD = -50000;

* 未调用init 初始化
ASR_ERROR_ONLINE_NOT_INITED = -50001;

* 异常才会出现，正常不出现
ASR_ONLINE_RECOGNIZE_ERROR = -50014;

* 录音设备开启异常
ASR_START_RECORD_ERROR = -50015;

* 连接服务失败，目前未使用，用比ASR_USC_CODE_CONNECT_SERVER_FAILED小的错误码替换
ASR_ONLINE_START_ERROR = -50016;

* SDK_UPLOAD 类型错误为上传文本失败错误信息
ASR_SDK_UPLOAD_ONESHOT_ERR_ILLEGAL_CHAR

* ASR_USC 错误类型为连接在线识别服务器的错误类型
ASR_USC_CODE_CONNECT_SERVER_FAILED  = -90001

#### TTS接口说明

##### 1、主要的类 UnisoundTtsEngine 接口

* 引擎初始化
boolean init()

* 设置引擎参数
*
* @param option {@link TtsOption}
* @return 返回具体的错误码 {@link TtsError}

int setTtsOption(TtsOption option, Object value)

* 播放一段buffer音频数据
void playBuffer(byte[] buffer,TtsPlayOption playOption)

* 播放text文本内容
void playTts(String text)

* 播放text文本内容
void playTts(String text, TtsPlayOption playOption)

* 暂停播放
void pause()

* 重新恢复播放
void resume()

* 停止播放
void stop()

* 设置音频输出接口，这个接口会替换内部的播放器，用这个接口可以自定义一个播放器
void setAudioTrack(@NonNull IAudioTrack audioTrack)

* 设置事件回调接口
* 接口详情可以查看ITtsEventListener

void setTtsEventListener(ITtsEventListener ttsEventListener)

* 释放TTS引擎资源
void release()

##### 2、TtsOption 参数说明

* 设置是否打印jni引擎log
* 是否必须设置：否
* 取值：false：关闭，true：打开
* 默认值：false
TTS_OPTION_PRINT_JNI_LOG(0),

* TTS tr地址
*  是否必须设置：否
* 默认值：ip:port
TTS_OPTION_INIT_TR_ADDRESS(JAVA_SDK),


* TTS tr地址
* 是否使用HTTPS。
TTS_OPTION_HTTPS_ENABLED(JAVA_SDK),

* 播放语速
* 是否必须设置：否
* 默认值：50
* 取值范围：｛0 - 100｝ 建议范围为50~70
TTS_OPTION_SPEED(1),

* 播放音量
* 是否必须设置：否
* 默认值：50
* 取值范围：｛0 - 100｝
TTS_OPTION_VOLUME(3),

* 调整音高（建议不做调整）
* 是否必须设置：否
* 取值范围：{0 - 100} 默认值50

TTS_OPTION_PITCH(2),

* 前静音长度
* 是否必须设置：否
* 默认值：50
* 取值范围：｛0 - 1000｝ 单位ms

TTS_OPTION_FRONT_SIL(6),

* 后静音长度
* 是否必须设置：否
* 默认值：50
* 取值范围：｛0 - 1000｝ 单位ms

TTS_OPTION_BACK_SIL(7),

*
* 调整声音亮度（数值越大，声音越亮，但是可能会造成机器音更明显）从 4.21.0 版本开始支持
* 是否必须设置：否
* 默认值：50
* 取值范围：｛50 - 100｝

TTS_OPTION_BRIGHT(11),

* 调整输出采样率（调整采样率会造成系统运算量） 适用于在线和离线
* 是否必须设置：否
* 默认值： 16000
* 取值范围：{8000-48000}

TTS_OPTION_SAMPLE_RATE(9),
*
* 设置音频流类型，适用于在线和离线
* 是否必须设置：否
* 默认值：{@link android.media.AudioManager#STREAM_MUSIC}
* 值范围：{0,8}

TTS_OPTION_STREAM_TYPE(JAVA_SDK),


* 播放结束延迟回调事件
* 默认值：300 单位ms

TTS_OPTION_END_DELAY(JAVA_SDK),

* 在线播放模式发音人
* 默认值：xiaoli
* 取值范围：｛
* 标准女声：xiaowen，xiaoli，xiaolijie
* 标准男生：xiaofeng，xiaoming
* 甜美女声：xuanxuan，sweet
* 台湾女声：lingling，lzl
* 童声女声：tangtang
* 童声男声：tiantian，boy
* Kiyo女声：kiyo｝
*

TTS_OPTION_VOICE_NAME(JNI_FUN),

* 通用唯一识别码
* 是否必须设置：是
* @see UnisoundTtsEngine.Builder#setDeviceUUID(String)

TTS_OPTION_INIT_DEVICE_UDID(22),


* 保存录音文件
* 取值 true,false
* 默认值false

TTS_OPTION_SAVE_TTS(JAVA_SDK),


* 设置是否将线程优先级设为urgentAudio，默认值false
* 默认值：false

TTS_OPTION_IS_URGENT_AUDIO(JAVA_SDK),


* 保存录音文件的路径
* 默认路径：/sdcard/unisound/

TTS_OPTION_SAVE_TTS_PATH(JAVA_SDK),


* 保存录音的文件类型
* @see TtsFileType
* 默认：pcm

TTS_OPTION_SAVE_TTS_TYPE(JAVA_SDK),


* 用户自定义读音标注文件路径
*@see UnisoundTtsEngine.Builder#setUserDicPath(String)

TTS_OPTION_INIT_USER_DICT_PATH(JNI_FUN),


* appkey,适用于在线
* 是否必须设置：是
*@see UnisoundTtsEngine.Builder#setAppKey(String)

TTS_OPTION_INIT_APPKEY(JNI_FUN),


* appSecret,适用于在线
*  是否必须设置：是
*  @see UnisoundTtsEngine.Builder#setAppSecret(String)

TTS_OPTION_INIT_APPSECRET(JNI_FUN),


* 每次播放时取的数据量
* 默认 1600 (采用率：22050 20毫秒大小)

TTS_OPTION_PLAY_BUFFER_SIZE(JAVA_SDK),

* 当引擎模式为混合模式时，在线请求超时时间
* 是否必须设置：否
* 取值范围 {100-1000}
* 默认500ms
* 
TTS_OPTION_MODE_MIX_TIMEOUT(JAVA_SDK),


* 是否播放tts
* 取值：true: 合成并播放，false: 只合成不播放
* 默认值：true

TTS_OPTION_PLAY_TTS(JAVA_SDK),

* TTS 延迟播放时间（包括第一次解析数据时间）   为解决开始播放卡顿问题
* 单位：ms 默认值：第一次解析数据时间时间不定

TTS_OPTION_PLAY_MAX_DELAY_TIME(JAVA_SDK),


* TTS 可能需要音素设置是否获取音素
* 默认值：false
* 单位：boolean类型

TTS_OPTION_PHONE_ENABLED(JAVA_SDK);

##### 3、TtsEvent 参数说明

* 初始化OK 
TTS_EVENT_INIT_SUCCESS = 2101;

* 开始合成事件
TTS_EVENT_ANALYZE_START = 2102;

* 结束合成事件
TTS_EVENT_ANALYZE_END = 2103;

* 开始缓冲
TTS_EVENT_BUFFER_BEGIN = 2104;

* 缓冲就绪
TTS_EVENT_BUFFER_END = 2105;

* 播放开始
TTS_EVENT_PLAY_START = 2106;

* 播放结束
TTS_EVENT_PLAY_END = 2107;

* 暂停事件
TTS_EVENT_PAUSE = 2108;

* 恢复事件
TTS_EVENT_RESUME = 2109;

* 停止事件
TTS_EVENT_STOP = 2111;

* 释放事件（引擎、模型、用户字典）
TTS_EVENT_RELEASE = 2112;

* 离线模型切换成功事件
TTS_EVENT_SWITCH_BACKEND_MODEL_SUCCESS = 2114;

* 播放时间
TTS_EVENT_PLAY_TIME = 2206;

* 保存录音文件成功
TTS_EVENT_SAVE_TTS_SUCCESS = 2207;

* 开始
TTS_EVENT_START = 2209;

* 取消
TTS_EVENT_CANCEL = 2210;

* 音素边界结果返回.结果类型样例如下

```
音素的结果例子
{"phoneme":[{"end":208,"phone":"sil","start":0,"tone":0,"type":8},
{"end":278,"phone":"zh","start":208,"tone":0,"type":1281},
{"end":487,"phone":"ong","start":278,"tone":1,"type":1284},
{"end":687,"phone":"sil","start":487,"tone":0,"type":8}],
"phonemee_restart":true,"sentence":"中"}
```
TTS_EVENT_PHONE_RESULT = 2301;

##### 4、TtsError 参数说明

* 初始化出现异常
TTS_ERROR_SDK_ONLINE_NOT_INIT = -10001;

* 未初始化，未调用init
TTS_ERROR_SDK_NOT_INIT = -10003;

* 播放器打开失败
TTS_ERROR_AUDIO_TRACK = -30000;

* 一般是文本设置失败，出现这个错误后，最后会出现TTS_ERROR_SET_TEXT
TTS_EVENT_PLAY_ERROR = -30001;

* TTS 设置文本出现错误，一般是网络问题导致设置在线文本失败
TTS_ERROR_SET_TEXT = 2301;

* 初始化失败
TTS_ERROR_INIT_ERROR = 2100;

*  保存录音文件失败
TTS_ERROR_SAVE_TTS_FAILED = 2208;


#### ASR使用说明

```
unisoundAsrEngine = new UnisoundAsrEngine(context,
                SdkParam.getInstance().getAppKey(),
                SdkParam.getInstance().getAppSecret(), true);
 
unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_VER,"3.2");
//是否开启NLU
unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_PRINT_JNI_LOG,true);
unisoundAsrEngine.init();

`
`
`
//开始识别
unisoundAsrEngine.startAsr()

`
`
`
//结束识别
unisoundAsrEngine.cancel()
`
`
`
//最后释放引擎
unisoundAsrEngine.release()

```


#### TTS使用说明


SDK使用的时候的代码片段

##### 1、初始化TTS
```

unisoundTtsEngine = new UnisoundTtsEngine.Builder()
        .setAppKey("设置appkey")
        .setAppSecret("设置appsecret")
        .setDeviceUUID("设备唯一标记")
        .setInitMode(UnisoundTtsMode.ONLINE)
        .build(getApplicationContext());
unisoundTtsEngine.setTtsEventListener(this);
unisoundTtsEngine.init();

```

##### 2、设置TTS播放音频的速度音色等

```

//设置速度
unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_SPEED, 50);
//设置音量
unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOLUME, 50);
//设置声音亮度
unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BRIGHT, 50);
//设置音高
unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_PITCH,50);
//设置采样率
unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_SAMPLE_RATE, 16000);

```

##### 3、设置文本播放内容

```
//简单播放
unisoundTtsEngine.playTts("播放的文本内容");
//可以播放的时候保存音频文件
unisoundTtsEngine.playTts("播放的文本内容",         
            new TtsPlayOption(
            //是否保存。//
            true, 
            //保存的文件名
            "ttsaudio", 
            //保存的文件路径 
            "/sdcard/tts/audio/",
            //保存的文件类型
             TtsOption.TtsFileType.WAV, 
             //是否需要播放内容
             false, 
             //音频的streamType。这里设置music。默认值就是music。
             AudioManager.STREAM_MUSIC))

```

##### 4、释放TTS资源

```

if (unisoundTtsEngine != null) {
   unisoundTtsEngine.release();
   unisoundTtsEngine = null;
}

```