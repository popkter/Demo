package com.popkter.collector

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.popkter.collector.ui.theme.POPCollectorTheme
import com.popkter.collector.view.DeskTopLeft
import com.popkter.collector.view.DeskTopRight
import com.popkter.collector.view.LineChart
import java.lang.Integer.max
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min

class ComposeActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 100
        private const val TAG = "ComposeActivity"
        private val WALL_PAPER_LIST = arrayListOf(R.mipmap.app_bg_2, R.mipmap.app_bg)
    }

    private var currentWallPaperIndex = 0

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (checkAndRequestPermissions(REQUEST_CODE_READ_EXTERNAL_STORAGE)) {
            viewModel.loadMusicFromLocal()
        }
        setContent {
            POPCollectorTheme {
                var wallpaper by remember { mutableIntStateOf(R.mipmap.app_bg) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown()
                                    // 检测到点击，先处理点击手势
                                    if (down.pressed) {
                                        viewModel.resetData()
                                    }
                                }
                            }
                            .pointerInput(Unit){
                                detectTapGestures {
                                    viewModel.resetData()
                                }
                            }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount > 50) {
                                        currentWallPaperIndex = max(0, currentWallPaperIndex - 1)
                                        wallpaper = WALL_PAPER_LIST[currentWallPaperIndex]
                                        Log.e(TAG, "onCreate: wallpaper= $wallpaper")
                                    }
                                    if (dragAmount < -50) {
                                        currentWallPaperIndex =
                                            min(WALL_PAPER_LIST.size - 1, currentWallPaperIndex + 1)
                                        wallpaper = WALL_PAPER_LIST[currentWallPaperIndex]
                                        Log.e(TAG, "onCreate: wallpaper= $wallpaper")
                                    }
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = wallpaper),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row {
                            DeskTopLeft(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxHeight()
                                    .weight(1F)
                                    .padding(horizontal = 10.dp)
                                    .padding(top = 10.dp), viewModel
                            )
                            DeskTopRight(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxHeight()
                                    .weight(1F),
                                viewModel
                            )
                        }
                    }

                }
            }
        }
    }


}

fun Activity.checkAndRequestPermissions(requestCode: Int): Boolean {
    return if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode
        )
        false
    } else {
        true
    }
}


fun formatTimestamp(timestamp: Long): String {
    // 将时间戳转换为 LocalDate
    val localDate = Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    // 定义日期格式
    val formatter = DateTimeFormatter.ofPattern("MM-dd")

    // 格式化日期并返回
    return localDate.format(formatter)
}


@Preview(showBackground = true)
@Composable
fun PreviewLineChart() {
    val temperatures = listOf(20f, 25f, 22f, 30f, 28f, 33f, 31f)
    val timestamps = listOf("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00")

    LineChart(temperatures = temperatures, timestamps = timestamps)
}

suspend fun PointerInputScope.detectMultitouchSwipeGesture(
    minPointers: Int,
    onSwipe: (SwipeDirection) -> Unit
) {
    Log.e("MainViewModel", "detectMultitouchSwipeGesture: onSwipe")
    awaitPointerEventScope {
        while (true) {
            // 等待直到检测到最少数量的触摸点
            val down = awaitFirstDown(requireUnconsumed = false)
            var pointerCount = 1

            // 监听指针事件，直到达到指定数量的触摸点
            while (pointerCount < minPointers) {
                val event = awaitPointerEvent()
                pointerCount = event.changes.size
            }

            // 当达到双指触摸时，开始检测水平滑动
            var initialOffset = Offset.Zero
            var totalDragDistance = 0f

            awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                initialOffset = change.position
                totalDragDistance += overSlop
            }?.let { change ->
                do {
                    val dragDistance = change.position.x - initialOffset.x
                    totalDragDistance += dragDistance

                    // 判断滑动方向
                    if (totalDragDistance > 100) {
                        onSwipe(SwipeDirection.Right)
                        break
                    } else if (totalDragDistance < -100) {
                        onSwipe(SwipeDirection.Left)
                        break
                    }

                    change.consume()
                } while (awaitPointerEvent().changes.any { it.pressed })
            }
        }
    }
}

enum class SwipeDirection {
    Left, Right
}
