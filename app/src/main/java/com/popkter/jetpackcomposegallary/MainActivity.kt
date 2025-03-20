package com.popkter.jetpackcomposegallary

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.popkter.jetpackcomposegallary.app_panel.AppListAdapter
import com.popkter.jetpackcomposegallary.app_panel.AppPanel
import com.popkter.jetpackcomposegallary.databinding.ActivityMainBinding
import com.popkter.jetpackcomposegallary.tool.getInstalledApps


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val appList by lazy { getInstalledApps(this) }

    private val adapter by lazy { AppListAdapter(appList) }

    private val binding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }
    private val dialog by lazy {
        AppPanel(this, adapter)
    }

//    private val PackageName = "com.tencent.wecarnavi_demo"
//    private val ActivityName = "com.tencent.wecarnavi.main.SplashActivity"

//    private val PackageName = "com.senseauto.localmultimodaldemo"
//    private val ActivityName = "com.senseauto.localmultimodaldemo.MainActivity"


//    private val packageName = "compose.popkter.robotface"
//    private val activityName = "compose.popkter.robotface.MainActivity"

    private val packageName = "com.popkter.jetpackcomposegallary"
    private val activityName = "com.popkter.jetpackcomposegallary.MainActivity2"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
//        setAsDefaultLauncher()
        binding.btn.setOnClickListener {
//            dialog.show()
            startClientApp(packageName, activityName)
        }

        Log.e(TAG, "onCreate 6")
    }


    private fun setAsDefaultLauncher() {
        startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
    }

    private fun startClientApp(clientPackageName: String, clientActivityName: String) {

        val intent = Intent().apply {
            component = ComponentName(clientPackageName, clientActivityName)
//            addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent, ActivityOptions.makeBasic().setLaunchBounds(Rect(0, 0, 800, 1200)).toBundle())
    }

    @SuppressLint("NewApi")
    private fun Context.getClientTaskId(clientPackageName: String) {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.appTasks

        for (task in tasks) {
            val baseIntent = task.taskInfo.baseIntent
            if (baseIntent.component?.packageName == clientPackageName) {
                val clientTaskId = task.taskInfo.taskId
//                Log.d("MainActivity", "Client App Task ID: $clientTaskId")
                return
            }
        }
    }

    fun openUrlInAdjacentWindow(url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent -> startActivity(intent) }
    }


}



