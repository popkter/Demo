package com.popkter.media

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.popkter.media.MediaPlayerExt.PlayStateChangeListener.Companion.STATE_READY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max


class MediaPlayerExt(context: Context) :
    CoroutineScope by CoroutineScope(Dispatchers.Main),
    ExoPlayer by ExoPlayer.Builder(context).build() {

    companion object {
        private const val TAG = "MusicPlayer"
    }

    // 播放列表
    private val _playlist = MutableStateFlow<List<MediaItem>>(emptyList())
    val playlist: StateFlow<List<MediaItem>> get() = _playlist.asStateFlow()

    // 歌词
    private val _lyrics = MutableSharedFlow<String>()
    val lyrics: SharedFlow<String> get() = _lyrics.asSharedFlow()

    private val progressUpdateInterval = 1000L // 每秒更新一次
    private var progressJob: Job? = null

    // 上一首、下一首的回调接口
    var loadListener: MusicLoader? = null

    // 播放状态改变的回调接口
    var playerStateListener: PlayStateChangeListener? = null

    // 播放器监听器
    private val playerListener = object : Player.Listener {

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.e(TAG, "onTimelineChanged: $timeline")
            val mediaItems = mutableListOf<MediaItem>()
            for (i in 0 until timeline.windowCount) {
                val window = Timeline.Window()
                timeline.getWindow(i, window)
                window.mediaItem.let { mediaItems.add(it) }
            }
            _playlist.value = mediaItems
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.e(TAG, "onPlaybackStateChanged: $playbackState")
            playerStateListener?.onPlayStateChange(playbackState)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Log.e(TAG, "onMediaItemTransition: $reason")
            playerStateListener?.onPlayStateChange(
                if (mediaItem == null) {
                    PlayStateChangeListener.STATE_ENDED
                } else {
                    PlayStateChangeListener.STATE_LOADING
                }
            )
            // 当媒体项切换时，可以在这里加载对应的歌词
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            //在此处加载歌曲信息
            if (currentMediaItem?.mediaMetadata == null){
                val newMediaItem  = currentMediaItem?.buildUpon()?.setMediaMetadata(mediaMetadata)?.build()
                newMediaItem?.let {
                    playerStateListener?.onPlayItemChange(it)
                    replaceMediaItem(currentMediaItemIndex, it)
                }
            }else{
                currentMediaItem?.let {
                    playerStateListener?.onPlayItemChange(it.buildUpon().setMediaMetadata(mediaMetadata).build())
                }
            }

        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onPlaylistMetadataChanged(mediaMetadata)
            Log.e(TAG, "onPlaylistMetadataChanged: ${mediaMetadata.title}")
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            Log.e(TAG, "onIsLoadingChanged: $isLoading ")
            super.onIsLoadingChanged(isLoading)
            if (playbackState == STATE_READY && isPlaying) {
                return
            } else {
                if (isLoading) {
                    playerStateListener?.onPlayStateChange(PlayStateChangeListener.STATE_LOADING)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.e(TAG, "onIsPlayingChanged: $isPlaying playbackState= $playbackState ")
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying){
                startProgressUpdates()
                playerStateListener?.onPlayStateChange(PlayStateChangeListener.STATE_PLAYING)
            }
            else if (playbackState == STATE_READY){
                stopProgressUpdates()
                playerStateListener?.onPlayStateChange(PlayStateChangeListener.STATE_PAUSING)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            playerStateListener?.onPlayStateChange(PlayStateChangeListener.STATE_ERROR)
        }

        override fun onEvents(player: Player, events: Player.Events) {
//            Log.e(TAG, "onEvents: $events")
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                if (player.currentMediaItem == null) {
                    // 播放列表为空，尝试加载上一首或下一首
                    when {
                        player.hasPreviousMediaItem() -> player.seekToPrevious()
                        player.hasNextMediaItem() -> player.seekToNext()
                        else -> {
                            launch {
                                // 调用回调接口
                                loadListener?.onLoadNext()?.let {
                                    player.playWhenReady = true
                                    player.prepare()
                                    player.setMediaItem(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        // 添加播放器监听器
        addListener(playerListener)
    }

    fun startProgressUpdates() {
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (isPlaying) {
                    playerStateListener?.onPlayProgressChange(currentPosition, duration)
                }
                delay(progressUpdateInterval)
            }
        }
    }

    fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    // 加载歌词的方法（示例实现）
    private fun loadLyrics(uri: String) {
        // 假设从网络或本地加载歌词，这里使用协程模拟
        CoroutineScope(Dispatchers.IO).launch {
            // 模拟加载歌词
            delay(1000)
            val lyricsContent = "这是一首歌的歌词内容，来自 $uri"
            _lyrics.emit(lyricsContent)
        }
    }

    // 播放控制方法
    override fun play() {
        playWhenReady = true
        prepare()
        launch {
            if (_playlist.value.isEmpty()) {
                loadListener?.onLoadNext()?.let {
                    setMediaItem(it)
                }
            }
        }
    }


    fun playPrevious() {
        launch {
            if (hasPreviousMediaItem()) {
                seekToPrevious()
            } else {
                loadListener?.onLoadPrevious()?.let {
                    val currentIndex = currentMediaItemIndex
                    addMediaItem(max(currentIndex - 1, 0), it)
                    seekToPreviousMediaItem()
                }
            }
        }
    }

    fun playNext() {
        launch {
            if (hasNextMediaItem()) {
                seekToNext()
            } else {
                loadListener?.onLoadNext()?.let {
                    addMediaItem(it)
                    seekToNextMediaItem()
                }
            }
        }
    }



    // 释放资源
    fun releasePlayer() {
        removeListener(playerListener)
        release()
    }


    /**
     * 上一曲/下一曲自定义加载
     */
    interface MusicLoader {
        suspend fun onLoadPrevious(): MediaItem
        suspend fun onLoadNext(): MediaItem
    }

    /**
     * 歌曲信息自定义加载
     */
    interface MusicInfoLoader {
        suspend fun loadAvatarUrl(): String
        suspend fun loadLyricsUrl(): String
        suspend fun loadArtist(): String
        suspend fun loadTitle(): String
        suspend fun loadAlbum(): String
    }

    /**
     * 播放信息状态回调
     */
    abstract class PlayStateChangeListener {
        companion object{
            const val STATE_IDLE = 1
            const val STATE_LOADING = 2
            const val STATE_READY = 3
            const val STATE_ENDED = 4
            const val STATE_PAUSING = 5
            const val STATE_PLAYING = 6
            const val STATE_ERROR = 7
        }

        open fun onPlayStateChange(state: Int){
            when(state){
                STATE_IDLE -> onStateIdle()
                STATE_LOADING -> onStateLoading()
                STATE_PAUSING -> onStatePausing()
                STATE_PLAYING -> onStatePlaying()
                STATE_ERROR -> onStateError()
                STATE_ENDED -> onStateCompleted()
                else -> {}
            }
        }

        open fun onPlayProgressChange(progress: Long, duration: Long) {}

        open fun onPlayItemChange(item: MediaItem) {}

        open fun onStateIdle() {}

        open fun onStateLoading() {}

        open fun onStatePausing() {}

        open fun onStatePlaying() {}

        open fun onStateError() {}

        open fun onStateCompleted() {}

    }
}
