package com.popkter.collector.ui.view

import android.text.TextPaint
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.popkter.collector.MUSIC_ITEM_COVER
import com.popkter.collector.viewmodel.MainViewModel
import com.popkter.collector.viewmodel.MainViewModel.Companion.MUSIC_LOADING
import com.popkter.collector.viewmodel.MainViewModel.Companion.MUSIC_PLAYING
import com.popkter.collector.R
import com.popkter.collector.entity.Poi
import com.popkter.collector.formatTimestamp
import com.popkter.collector.viewmodel.ChatViewModel
import com.popkter.voice_assistant.base.BaseAsrHelper
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "DeskTopLeft"

private val buttons =
//    arrayOf("讲笑话", "认场景", "搜地点", "查天气", "播音乐", "MOCK", "TODO", "0", "30", "60")
    arrayOf("查天气","0", "30", "60")

@Composable
fun DeskTopLeft(modifier: Modifier, viewModel: ChatViewModel) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier.fillMaxHeight()
    ) {
        DataResultView(
            Modifier
                .wrapContentWidth()
                .align(Alignment.TopStart), viewModel
        )

        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomStart)
        ) {
            LazyVerticalGrid(
                GridCells.Fixed(3),
            ) {
                items(buttons) {
                    LoadDataButton(it) {
                        when (it) {
//                            "讲笑话" -> viewModel.loadNovelData()
//                            "认场景" -> viewModel.loadSceneDemoData()
//                            "搜地点" -> viewModel.loadPoiDemoData()
                            "查天气" -> viewModel.loadWeather("上海市今天晚上的天气怎么样")
//                            "播音乐" -> viewModel.loadMusicDemoData()
//                            "MOCK" -> {
//                                scope.launch {
//                                    arrayOf("我心情很郁闷，","我想听首歌。","今天天气怎么样","你知道么，哈哈","你好呀","TTS。").forEach{ str->
//                                        delay(50)
//                                        viewModel.playUniSoundChunkTts(str)
//                                    }
//                                }
//                            }
//                            "TODO" -> {
//                                viewModel.todo()
//                            }
                            "0" -> {
                                viewModel.updateDuplexTimeout(BaseAsrHelper.DuplexTimeout.ZERO)
                            }

                            "30" -> {
                                viewModel.updateDuplexTimeout(BaseAsrHelper.DuplexTimeout.HALF_ONE_MINUTE)
                            }

                            "60" -> {
                                viewModel.updateDuplexTimeout(BaseAsrHelper.DuplexTimeout.ONE_MINUTE)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun DataResultView(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
) {

    val scope = rememberCoroutineScope()
    val text by viewModel.novelResult.collectAsState("")
    val imageUrl by viewModel.imageResult.collectAsState("")
    val listPoi by viewModel.poiResultFlow.collectAsState(emptyList())
    val listWeather by viewModel.weatherResult.collectAsState(null)

    val canDisplayMusicCard by viewModel.showMusicCard.collectAsState(false)
    val isPlaying by viewModel.musicPlayStatus.collectAsState(false)

    val shape by rememberUpdatedState(
        if (text.isEmpty() && listPoi.isEmpty() && listWeather == null) RoundedCornerShape(
            30.dp
        ) else RoundedCornerShape(
            15.dp
        )
    )

    val scrollState = rememberScrollState()
    // 当 text 更新时，自动滚动到底部
    LaunchedEffect(text) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Surface(
        modifier = modifier
            .padding(2.dp)
            .border(
                width = 1.dp, brush = Brush.verticalGradient(
                    colors = listOf(Color.Red, Color.White, Color.Gray)
                ), shape = shape
            )
            .clip(shape),
        color = Color.DarkGray.copy(alpha = 0.5F),
        contentColor = Color.DarkGray.copy(alpha = 0.5F)
    ) {
        Box(
            modifier = modifier
                .sizeIn(0.dp, 0.dp, 400.dp, 400.dp)
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ) { _, _ ->
//                    scope.coroutineContext.cancelChildren()
//                    //different delay
//                    if (listPoi.isNotEmpty()) {
//
//                    } else {
//                        scope.launch {
//                            delay(20000)
//                            viewModel.resetData()
//                        }
//                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        scope.coroutineContext.cancelChildren()
                        scope.launch {
                            delay(2000)
                            viewModel.resetData()
                        }
                    }
                },
            contentAlignment = Alignment.TopStart,
        ) {
            //背景模糊，不生效
            /*Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = BlurEffect(
                            radiusX = 105f,
                            radiusY = 105f
                        )
                    }
                    .background(Color.Black.copy(alpha = 0.5f))
            )*/

            Column {

                // 当 text 和 listPoi 都为空时，显示VPA
                if (text.isEmpty() && listPoi.isEmpty() && listWeather == null && !canDisplayMusicCard) {
                    LottieAnimationView(
                        modifier
                            .size(60.dp, 60.dp)
                            .clickable {
                                viewModel.startWakeUp()
                            },
                        "Idle.json"
                    )
                }
                // 当 text 不为空时渲染内容
                if (text.isNotEmpty()) {
                    Text(
                        modifier = modifier
                            .heightIn(0.dp, 200.dp)
                            .verticalScroll(scrollState, true)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        color = Color.White,
                        text = text
                    )
                }
                if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Inside,  // 根据需要设置内容缩放模式
                        modifier = modifier
                            .sizeIn(50.dp, 50.dp, 200.dp, 200.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
                // 当 listPoi 不为空时渲染内容
                if (listPoi.isNotEmpty()) {
                    LazyColumn(
                        modifier = modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        items(listPoi) {
                            PoiItemCard(it)
                        }
                    }
                }


                listWeather?.let {
                    LineChart(modifier,listWeather!!.map { it.first },listWeather!!.map { it.second })
                }

                if (canDisplayMusicCard) {
                    MusicPlayerCard(viewModel, isPlaying)
                }
            }
        }
    }
}


@Composable
fun LoadDataButton(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .wrapContentSize()
    ) {
        Button(onClick = {
            onClick()
        }) {
            Text(
                text = text
            )
        }
    }
}

@Composable
fun PoiItemCard(poi: Poi, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    Toast
                        .makeText(context, poi.name, Toast.LENGTH_SHORT)
                        .show()
                })
            },
        shape = RoundedCornerShape(10.dp),
    ) {

        Row(
            modifier = modifier.padding(10.dp)
        ) {

            Column(modifier = modifier.weight(1F)) {
                NetworkImage(
                    url = poi.cover,
                    modifier = modifier
                        .fillMaxHeight()
                        .aspectRatio(1F)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Column(
                modifier = modifier
                    .weight(3F)
                    .padding(start = 10.dp)
            ) {
                Row {
                    Text(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier,
                        text = poi.name
                    )
                }

                Row {
                    Text(
                        modifier = modifier,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Thin,
                        text = "${poi.distance}公里"
                    )
                }

                Row {
                    Text(
                        fontSize = 16.sp, modifier = modifier, text = poi.address
                    )
                }
            }
        }
    }

}

@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier) {/*setSingletonImageLoaderFactory {context->
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()
    }*/
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        contentScale = ContentScale.Crop,  // 根据需要设置内容缩放模式
        modifier = modifier,
    )
}

@Composable
fun LottieAnimationView(modifier: Modifier = Modifier, resource: String) {

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(resource))

    LottieAnimation(
        composition = composition, iterations = LottieConstants.IterateForever, modifier = modifier
    )
}

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    temperatures: List<Float>, // 温度数据
    timestamps: List<String> // 时间数据
) {
    val maxTemperature = temperatures.maxOrNull() ?: 0f
    val minTemperature = temperatures.minOrNull() ?: 0f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        val width = size.width
        val height = size.height

        val lineHeight = height - 25.dp.toPx()
        // 计算折线图的比例
        val xInterval = (width - 10) / (temperatures.size - 1)
        val yScale = lineHeight / (maxTemperature - minTemperature) / 2

        // 绘制折线
        for (i in 0 until temperatures.size - 1) {
            drawLine(
                Color.Magenta,
                Offset(i * xInterval, lineHeight - (temperatures[i] - minTemperature) * yScale),
                Offset(
                    (i + 1) * xInterval,
                    lineHeight - (temperatures[i + 1] - minTemperature) * yScale
                ),
                strokeWidth = 5f
            )
        }

        // 绘制坐标轴
        drawLine(
            Color.White, Offset(0f, height - 20), Offset(width, height - 20), strokeWidth = 1f
        ) // X轴

        var hasDrawMax = false
        var hasDrawMin = false


        // 绘制时间戳和温度
        for (i in temperatures.indices) {
            val xPosition = i * xInterval - 20f
            val yPosition = lineHeight - (temperatures[i] - minTemperature) * yScale

            //绘制起止点和极值
            if ((temperatures[i] == temperatures.max() && !hasDrawMax) || (temperatures[i] == temperatures.min() && !hasDrawMin) || i == 0 || i == temperatures.size - 1) {
                if (temperatures[i] == temperatures.min()) {
                    hasDrawMin = true
                }
                if (temperatures[i] == temperatures.max()) {
                    hasDrawMax = true
                }
                // 绘制温度
                drawContext.canvas.nativeCanvas.drawText("${temperatures[i]}°C",
                    xPosition,
                    yPosition - 10, // 温度值在点上方显示
                    TextPaint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 24F
                    })
            }

            //绘制起止点
            if (i == 0 || i == temperatures.size - 1) {
                // 绘制时间戳
                drawContext.canvas.nativeCanvas.drawText(timestamps[i],
                    xPosition,
                    height + 20, // 调整文本垂直位置
                    TextPaint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 26F
                    })
            }


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerCard(
    viewModel: ChatViewModel,
    isPlaying: Comparable<*>,
) {
    val title by viewModel.musicInfo.collectAsState("歌曲名")
    val progress by viewModel.musicProgress.collectAsState(0L)
    val duration by viewModel.musicDuration.collectAsState(0L)
    val imageUrl by rememberUpdatedState(MUSIC_ITEM_COVER)


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
            .padding(10.dp)
            .height(100.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(15.dp),
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.5F))
                    .blur(50.dp)
            )
            Row(
                modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
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

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1F)
                    )
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
                                .size(24.dp)
                                .clickable {
                                    viewModel.playPrevious()
                                },
                            tint = Color.Black
                        )

                        Box(
                            modifier = Modifier
                                .size(24.dp)
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
                                    tint = Color.Black
                                )
                            }
                        }


                        // 下一曲
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.skip_next),  // 替换为实际资源
                            contentDescription = "Next", modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    viewModel.playNext()
                                }, tint = Color.Black
                        )
                    }


                }
            }
        }
    }
}