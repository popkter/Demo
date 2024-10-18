package com.senseauto.localmultimodaldemo.entity

import androidx.annotation.DrawableRes

data class MainSceneItem(
    @DrawableRes val naviIcon:Int,
    @DrawableRes val titleIcon: Int,
    val title: String,
    val scenes: List<SubSceneItem>
)

sealed class SceneType

data object CameraScene : SceneType()
data object PicScene : SceneType()

data class SubSceneItem(
    @DrawableRes val sceneCover: Int,
    val hints: List<HintItem>,
    val sceneType: SceneType = PicScene
)

data class HintItem(
    val hint: String,
    val prompt: String
)