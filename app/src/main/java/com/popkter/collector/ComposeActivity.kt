package com.popkter.collector

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.popkter.collector.ui.view.DeskTopLeft
import com.popkter.collector.ui.view.DeskTopRight
import com.popkter.collector.ui.view.LineChart
import com.popkter.collector.viewmodel.ChatViewModel
import com.popkter.collector.viewmodel.MainViewModel
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
        private val permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val grantedPermissions = permissions.filterValues { it }.keys
            val deniedPermissions = permissions.filterValues { !it }.keys

            if (grantedPermissions.isNotEmpty()) {
                Toast.makeText(this, "以下权限已授予: $grantedPermissions", Toast.LENGTH_SHORT).show()
            }

            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(this, "以下权限被拒绝: $deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }

    private val viewModel: ChatViewModel by viewModels()

    private var currentWallPaperIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestMultiplePermissionsLauncher.checkPermissions(this, permissions)
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
                            .pointerInput(Unit) {
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
        viewModel.initVui()
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

fun ActivityResultLauncher<Array<String>>.checkPermissions(
    context: Context,
    permissions: List<String>
) {
    // 检查权限
    val permissionsToRequest = mutableListOf<String>()
    permissions.forEach {permission->
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(permission)
        }
    }
    // 请求权限
    if (permissionsToRequest.isNotEmpty()) {
        launch(permissionsToRequest.toTypedArray())
    } else {
        Toast.makeText(context, "所有权限已授予", Toast.LENGTH_SHORT).show()
    }
}

enum class SwipeDirection {
    Left, Right
}
