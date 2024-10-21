package com.senseauto.localmultimodaldemo.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.entity.SubSceneItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntelligenceScene(
    index: Int,
    subSceneItem: SubSceneItem,
    listState: PagerState,
    viewModel: MainViewModel) {
}