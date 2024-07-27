package com.popkter.jetpackcomposegallary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.popkter.jetpackcomposegallary.app_panel.AppListAdapter
import com.popkter.jetpackcomposegallary.app_panel.AppPanel
import com.popkter.jetpackcomposegallary.databinding.ActivityMainBinding
import com.popkter.jetpackcomposegallary.tool.getInstalledApps


class MainActivity : AppCompatActivity() {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            dialog.show()
        }
    }


}

