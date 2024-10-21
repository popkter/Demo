package com.senseauto.localmultimodaldemo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.senseauto.lib_sound.ability.IFlytekAbilityManager
import com.senseauto.localmultimodaldemo.constant.ListData
import com.senseauto.localmultimodaldemo.ui.theme.LocalMultiModalDemoTheme
import com.senseauto.localmultimodaldemo.ui.view.HomeBottomNav
import com.senseauto.localmultimodaldemo.ui.view.HomeLeftContent
import com.senseauto.localmultimodaldemo.ui.view.HomeRightContent

class MainActivity : ComponentActivity() {

    private val permissionList = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.BLUETOOTH_CONNECT
    )


    private val viewModel: MainViewModel by viewModels()

    private var allPermissionGranted = false

    // 注册多个权限请求的 Launcher
    private val multiplePermissionsLauncher =  registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.all { it.value }.let {
            allPermissionGranted = it
            if (!allPermissionGranted){
                Toast.makeText(this@MainActivity,"有权限未授权，请在设置手动开启",Toast.LENGTH_SHORT).show()
            }else{
                Log.e(TAG, "onCreate: 权限请求成功")
//                IFlytekAbilityManager.getInstance().initializeSdk(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI()
        setContent {
            LocalMultiModalDemoTheme {
                    val selectIndex by viewModel.tabIndex.collectAsState(0)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {

                        Image(
                            painterResource(R.drawable.main_bg),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.3F, 1.06F)
                        )
                        Image(
                            painter = painterResource(R.drawable.main_logo),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(top = 25.dp, start = 40.dp)
                                .size(120.dp, 30.dp)
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 40.dp)
                                .padding(top = 60.dp)
                                .background(Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(4F),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1F)
                                        .fillMaxHeight()
                                ) {
                                    HomeLeftContent(
                                        mainSceneItem = ListData[selectIndex],
                                        viewModel = viewModel
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1F)
                                        .fillMaxHeight()
                                ) {
                                    HomeRightContent(
                                        viewModel = viewModel
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1F)
                            ) {
                                HomeBottomNav(ListData, selectIndex) { index ->
                                    viewModel.updateTabIndex(index)
                                }
                            }
                        }
                    }

                }

            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

            allPermissionGranted = permissionList.map {
                ContextCompat.checkSelfPermission(this@MainActivity,it) == PermissionChecker.PERMISSION_GRANTED
            }.any{it}

            if (!allPermissionGranted){
                multiplePermissionsLauncher.launch(permissionList.toTypedArray())
            }
        }
        IFlytekAbilityManager.getInstance().initializeSdk(this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object{
        private const val TAG = "MainActivity"
    }
}

/*

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
}*/
