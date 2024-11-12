# MusicPlayer

A music player for Android Power on ExoPlayer

## 功能详解
1. 获取当前的播放列表，以及当播放列表变化时通知观察者

```kotlin
// 订阅播放列表变化
musicPlayer.playlist.collect { playlist ->
// 更新UI或其他操作
}
```
2. 获取当前播放状态的变化，以及当播放状态变化时通知观察者

```kotlin
// 订阅播放状态变化
musicPlayer.playbackState.collect { state ->
// 根据状态更新UI，如播放、暂停按钮
}
```

3. 加载上一首、下一首的接口

```kotlin
musicPlayer.onLoadPreviousNextListener =
   object : MusicPlayer.OnLoadPreviousNextListener { 
       override fun onLoadPrevious() { 
       // 实现加载上一首的方法，如从网络或数据库加载 
       } 
       override fun onLoadNext() {
        // 实现加载下一首的方法 
       }
}
```

4. 播放、暂停、上一首、下一首
> 提供 play()、pause()、playPrevious()、playNext() 方法，直接调用 ExoPlayer 的相应方法。

5. 播放列表的循环模式控制
   实现方式：

提供 setRepeatMode(repeatMode: Int) 和 getRepeatMode() 方法，使用 ExoPlayer 的 repeatMode 属性。
循环模式常量：

```kotlin
Player.REPEAT_MODE_OFF       // 不循环
Player.REPEAT_MODE_ONE       // 单曲循环
Player.REPEAT_MODE_ALL       // 列表循环
```

6. 音量控制
>提供 setVolume(volume: Float) 和 getVolume() 方法，音量范围限制在 0.0f 到 1.0f。
7. 播放进度控制
> 提供 seekTo(positionMs: Long) 方法，用于跳转到指定位置。
> 
> 提供 getCurrentPosition() 和 getDuration() 方法，获取当前播放位置和总时长。
 
8. 操作播放列表
> 提供 addMediaItem()、addMediaItems()、removeMediaItem()、moveMediaItem()、clearMediaItems() 等方法，操作 ExoPlayer 的媒体项列表。
9. 歌词通知给观察者
```kotlin
复制代码
// 订阅歌词变化
musicPlayer.lyrics.collect { lyricsContent ->
// 显示歌词内容
}
```
## 使用示例

```kotlin
复制代码
// 初始化播放器
val musicPlayer = MusicPlayer(context)

// 添加播放列表
val mediaItem1 = MediaItem.Builder()
.setUri(Uri.parse("https://example.com/song1.mp3"))
.setMediaMetadata(MediaMetadata.Builder()
.setTitle("歌曲1")
.setArtist("歌手1")
.setExtras(Bundle().apply {
putString("LYRICS_URI", "https://example.com/song1.lrc")
})
.build())
.build()

val mediaItem2 = MediaItem.Builder()
.setUri(Uri.parse("https://example.com/song2.mp3"))
.setMediaMetadata(MediaMetadata.Builder()
.setTitle("歌曲2")
.setArtist("歌手2")
.setExtras(Bundle().apply {
putString("LYRICS_URI", "https://example.com/song2.lrc")
})
.build())
.build()

musicPlayer.addMediaItems(listOf(mediaItem1, mediaItem2))

// 开始播放
musicPlayer.play()

// 监听播放状态
lifecycleScope.launch {
musicPlayer.playbackState.collect { state ->
// 更新UI
}
}

// 监听播放列表变化
lifecycleScope.launch {
musicPlayer.playlist.collect { playlist ->
// 更新播放列表UI
}
}

// 监听歌词变化
lifecycleScope.launch {
musicPlayer.lyrics.collect { lyricsContent ->
// 显示歌词
}
}

// 设置上一首、下一首的加载回调
musicPlayer.onLoadPreviousNextListener = object : MusicPlayer.OnLoadPreviousNextListener {
override fun onLoadPrevious() {
// 加载上一首逻辑
}

    override fun onLoadNext() {
        // 加载下一首逻辑
    }
}

// 设置循环模式
musicPlayer.setRepeatMode(Player.REPEAT_MODE_ALL)

// 调整音量
musicPlayer.setVolume(0.8f)

// 调整播放进度
musicPlayer.seekTo(60_000) // 跳转到1分钟处
```
## 注意事项
协程使用：在使用 collect 订阅 Flow 时，确保在合适的协程作用域中，例如 lifecycleScope。

资源释放：在不再需要播放器时，调用 musicPlayer.release() 释放资源。

线程安全：ExoPlayer 的方法通常是线程安全的，但如果在多线程环境中操作播放器，需要注意同步问题。

错误处理：实现 onPlayerError 回调，处理播放过程中可能发生的错误，例如网络异常、解码错误等。

歌词加载：loadLyrics() 方法中的实现需要根据实际情况编写，例如从网络或本地文件加载歌词。

媒体元数据：MediaItem 的 MediaMetadata 中可以存储更多信息，如专辑封面、发行年份等，方便在 UI 中展示。

## TODO

音频焦点管理：处理音频焦点的获取和释放，确保在接听电话或其他应用需要音频时正确暂停和恢复播放。

通知栏和锁屏控制：集成 MediaSession，在通知栏和锁屏界面显示播放控制。

均衡器和音效：集成音频效果器，提供均衡器、重低音等音效调节功能。

缓存和预加载：使用 ExoPlayer 的缓存功能，缓存音频数据，提升播放体验。

多媒体格式支持：确保支持各种音频格式，如 MP3、AAC、FLAC 等。