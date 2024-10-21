package com.senseauto.lib_sound.ability

import android.content.Context
import android.util.Log
import com.iflytek.aikit.core.AiHelper
import com.iflytek.aikit.core.BaseLibrary
import com.iflytek.aikit.core.ErrType
import com.senseauto.lib_sound.R
import com.senseauto.lib_sound.tool.AssetsUtils
import kotlin.concurrent.thread

/**
 * @Desc: 讯飞语音初始化辅助类
 * @Author leon
 * @Date 2023/5/11-17:24
 * Copyright 2023 iFLYTEK Inc. All Rights Reserved.
 */
class IFlytekAbilityManager private constructor() {
    val TAG = "IFlytekAbilityManager"
    companion object {

        //在线授权校验间隔时长，默认为300s，可自定义设置，最短为60s，单位 秒
        private const val AUTH_INTERVAL = 333

        @Volatile
        private var instance: IFlytekAbilityManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: IFlytekAbilityManager().also { instance = it }
            }
    }

    /**
     * 初始化sdk
     * 只需要初始化一次
     */
    fun initializeSdk(context: Context) {
        Log.e(TAG, "initializeSdk: ", )
        //鉴权
        AiHelper.getInst().registerListener { type, code ->
            Log.d(TAG, "引擎初始化状态 ${type == ErrType.AUTH && code == 0}")
        }
        thread {
            val dir = context.getExternalFilesDir("iflytekAikit/ed")
            val root = dir?.absolutePath ?: context.cacheDir.absolutePath
            //基本模型拷贝
            AssetsUtils.copyDirsToPath(
                context,
                "encn",
                root + "/encn"
            )
            Log.i(TAG, "iflytek root path: $root")
            val params = BaseLibrary.Params.builder()
                .appId(context.resources.getString(R.string.appId))
                .apiKey(context.resources.getString(R.string.apiKey))
                .apiSecret(context.resources.getString(R.string.apiSecret))
                .workDir(root)
                .iLogMaxCount(1)
                .authInterval(AUTH_INTERVAL)
                .ability(engineIds())
                .build()

            AiHelper.getInst().init(context, params)
        }
    }

    /**
     * 添加所需的能力引擎id,多个能力用;隔开，如"xxx;xxx"
     */
    private fun engineIds() = listOf(
        AbilityConstant.ED_ENCN_ID
    ).joinToString(separator = ";")
}