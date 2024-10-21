package com.senseauto.lib_sound.tool

import android.content.Context
import android.util.Log
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object AssetsUtils {
    private const val TAG = "AssetsUtils"
    fun copyDirsToPath(context: Context, srcPath: String, destPath: String) {
        val ctx = context.applicationContext
        val dir = File(destPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try {
            val list = ctx.assets.list(srcPath)
            for (fileName in list!!) {
                copyFileToPath(ctx, "$srcPath/$fileName", "$destPath/$fileName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 把assets下的某个文件copy到指定文件
     *
     * @param srcFile  原文件路径
     * @param destFile 目标文件路径
     */
    fun copyFileToPath(context: Context, srcFile: String, destFile: String): Boolean {
        Log.d(TAG, "copy path:$srcFile new path:$destFile")
        val ctx = context.applicationContext
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        return try {
            val dest = File(destFile)
            // 目标文件存在且跟源文件一样,直接跳过
            if (dest.exists()) {
                Log.d(TAG, "copyFileToPath: destFile is exists, skip it")
            } else {
                inputStream = ctx.assets.open(srcFile)
                val destDat = File("$destFile.dat")
                outputStream = FileOutputStream(destDat)
                val buffer = ByteArray(1024)
                var read = inputStream.read(buffer)
                while (read != -1) {
                    outputStream.write(buffer, 0, read)
                    read = inputStream.read(buffer)
                }
                outputStream.flush()
                destDat.renameTo(dest)
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "copyFileToPath: error " + e.localizedMessage)
            false
        } finally {
            closeSafety(inputStream, outputStream)
        }
    }

    private fun closeSafety(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            try {
                closeable?.close()
            } catch (e: IOException) {
                // ignore
            }
        }
    }
}