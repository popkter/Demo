package com.senseauto.localmultimodaldemo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.senseauto.localmultimodaldemo.ui.theme.LocalMultiModalDemoTheme
import com.senseauto.localmultimodaldemo.ui.view.DemoData
import com.senseauto.localmultimodaldemo.ui.view.HomeView

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var cameraPermissionGranted by remember { mutableStateOf(false) }
            var audioPermissionGranted by remember { mutableStateOf(false) }

            // 注册多个权限请求的 Launcher
            val multiplePermissionsLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                // 检查每个权限是否被授予
                cameraPermissionGranted = permissions[android.Manifest.permission.CAMERA] ?: false
                audioPermissionGranted =
                    permissions[android.Manifest.permission.RECORD_AUDIO] ?: false
            }


            // 检查初始权限状态
            LaunchedEffect(Unit) {
                cameraPermissionGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity, android.Manifest.permission.CAMERA
                ) == PermissionChecker.PERMISSION_GRANTED

                audioPermissionGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity, android.Manifest.permission.RECORD_AUDIO
                ) == PermissionChecker.PERMISSION_GRANTED

                if (!cameraPermissionGranted || !audioPermissionGranted) {
                    multiplePermissionsLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.RECORD_AUDIO
                        )
                    )
                }
            }


            LocalMultiModalDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeView(Modifier.padding(innerPadding), DemoData, viewModel)
                }
            }
        }
    }
}


@Preview(
    name = "Landscape Preview",
    widthDp = 1920,
    heightDp = 1080,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
fun GreetingPreview() {
    LocalMultiModalDemoTheme {
        HomeView(Modifier, DemoData, MainViewModel())
    }
}