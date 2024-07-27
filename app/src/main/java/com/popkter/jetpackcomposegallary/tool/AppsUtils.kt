package com.popkter.jetpackcomposegallary.tool

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast


fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val packages = pm.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
    val appList = ArrayList<AppInfo>()

    for (packageInfo in packages) {
        val launchIntent = pm.getLaunchIntentForPackage(packageInfo.packageName)
        if (launchIntent != null) {
            val name = packageInfo.loadLabel(pm).toString()
            val icon = packageInfo.loadIcon(pm)
            val packageName = packageInfo.packageName
            appList.add(AppInfo(name, icon, packageName))
        }
    }


    return appList
}

fun launchApp(context: Context, packageName: String) {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)

    if (launchIntent != null) {
        context.startActivity(launchIntent)
    } else {
        Toast.makeText(context, "无法启动应用：$packageName", Toast.LENGTH_LONG).show()
    }
}

data class AppInfo(val name: String, val icon: Drawable, val packageName: String)