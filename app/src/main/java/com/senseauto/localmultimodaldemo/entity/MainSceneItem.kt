package com.senseauto.localmultimodaldemo.entity

import androidx.annotation.DrawableRes

data class MainSceneItem(
    @DrawableRes val naviIcon:Int,
    @DrawableRes val titleIcon: Int,
    val title: String,
    val scenes: List<SubSceneItem>,
    val sceneType: MainSceneType = GreetingScene
)


data class SubSceneItem(
    @DrawableRes val sceneCover: Int,
    val hints: List<HintItem>,
    val sceneType: SubSceneType = PicScene
)

data class HintItem(
    val hint: String,
    val prompt: String
)

sealed class SubSceneType

data object CameraScene : SubSceneType()
data object PicScene : SubSceneType()

sealed class MainSceneType
//拟人问候
data object GreetingScene : MainSceneType()
//智能情境识别
data object SceneIdentifyScene : MainSceneType()
//危险感知
data object HazardPerceptionScene : MainSceneType()
//电子闺蜜
data object EGirlfriendsScene : MainSceneType()
//POI识别
data object POIIdentifyScene : MainSceneType()
//交警手势
data object PoliceGestureIdentifyScene : MainSceneType()
//行车情景感知
data object TrafficIdentifyScene : MainSceneType()
//路况识别
data object RoadStatusIdentifyScene : MainSceneType()
//泊车安全
data object PilotIdentifyScene : MainSceneType()
//车道识别
data object DedicatedLanesIdentifyScene : MainSceneType()
//意图理解
data object IntentIdentifyScene : MainSceneType()
