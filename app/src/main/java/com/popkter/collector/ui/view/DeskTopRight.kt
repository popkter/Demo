package com.popkter.collector.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.popkter.collector.MUSIC_ITEM_COVER
import com.popkter.collector.viewmodel.MainViewModel
import com.popkter.collector.viewmodel.MainViewModel.Companion.MUSIC_LOADING
import com.popkter.collector.viewmodel.MainViewModel.Companion.MUSIC_PLAYING
import com.popkter.collector.R

@Composable
fun DeskTopRight(modifier: Modifier, viewModel: MainViewModel) {

    val canDisplayMusicCard by viewModel.showMusicInWidget.collectAsState(false)
    val isPlaying by viewModel.musicPlayStatus.collectAsState(false)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {

        AnimatedVisibility(
            visible = canDisplayMusicCard,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
            exit = fadeOut()
        ) {
            MusicPlayerWidgetCard(viewModel, isPlaying)
        }
    }
}

@Composable
fun WeatherCard(data: Any) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerWidgetCard(
    viewModel: MainViewModel,
    isPlaying: Comparable<*>,
) {
    val title by viewModel.musicInfo.collectAsState("歌曲名")
    val progress by viewModel.musicProgress.collectAsState(0L)
    val duration by viewModel.musicDuration.collectAsState(0L)
    val imageUrl by rememberUpdatedState(
        viewModel.currentMediaItem?.mediaMetadata?.artworkUri?.toString() ?: MUSIC_ITEM_COVER
    )


    val playIcon by rememberUpdatedState(
        if (isPlaying == MUSIC_PLAYING) {
            ImageVector.vectorResource(id = R.drawable.pause_circle)
        } else {
            ImageVector.vectorResource(id = R.drawable.play_circle)
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
//            .shadow(100.dp, RoundedCornerShape(10.dp), true, Color.White, Color.Black)
            .padding(10.dp)
            .clip(RoundedCornerShape(15.dp))
            .height(200.dp),
        color = Color.DarkGray.copy(alpha = 0.5F),
        contentColor = Color.DarkGray.copy(alpha = 0.5F)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
        ) {
            //背景模糊，不生效
            /*Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.DarkGray.copy(alpha = 0.5F))
                    .blur(50.dp)
            )*/

            Column(modifier = Modifier.padding(10.dp)) {
                Row {
                    // 音乐封面
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp)
                    )
                }

                // 进度条
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    value = progress.toFloat(),
                    valueRange = 0F..duration.toFloat(),
                    onValueChange = {
                        viewModel.seekTo(it.toLong())
                    },
                    onValueChangeFinished = {
                        viewModel.seekTo(isFinish = true)
                    },
                    /*                        colors = SliderDefaults.colors(
                                                thumbColor = Color.LightGray,
                                                activeTrackColor = Color.Black,
                                                inactiveTrackColor = Color.LightGray,
                                            ),*/
                    thumb = {
                        SliderDefaults.Thumb(
                            modifier = Modifier.alpha(0f),  // 设置透明度为 0
                            interactionSource = remember { MutableInteractionSource() },
                            thumbSize = DpSize(0.dp, 0.dp)  // 设置宽度和高度为 0
                        )
                    },
                )

                // 播放/暂停和进度条
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    // 上一曲
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.skip_previous),
                        contentDescription = "Previous",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                viewModel.playPrevious()
                            },
                        tint = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // 播放/暂停

                        if (isPlaying == MUSIC_LOADING) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        viewModel.pause()
                                    },
                            )
                        } else {
                            Icon(
                                imageVector = playIcon,
                                contentDescription = "Play/Pause",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (isPlaying == MUSIC_PLAYING) {
                                            viewModel.pause()
                                        } else {
                                            viewModel.play()
                                        }
                                    },
                                tint = Color.White
                            )
                        }
                    }


                    // 下一曲
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.skip_next),  // 替换为实际资源
                        contentDescription = "Next",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                viewModel.playNext()
                            },
                        tint = Color.White
                    )
                }

            }
        }
    }
}


/*

@Composable
@Preview
fun DeskTopRightPreview() {
    DeskTopRight(modifier = Modifier, viewModel = MainViewModel())
}*/
