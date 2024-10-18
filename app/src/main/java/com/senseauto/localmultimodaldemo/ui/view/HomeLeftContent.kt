package com.senseauto.localmultimodaldemo.ui.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.entity.CameraScene
import com.senseauto.localmultimodaldemo.entity.MainSceneItem
import com.senseauto.localmultimodaldemo.entity.PicScene

@Composable
fun HomeLeftContent(mainSceneItem: MainSceneItem, viewModel: MainViewModel) {
    Column(modifier = Modifier.width(894.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(37.5.dp)),
                contentScale = ContentScale.Crop,
                painter = painterResource(mainSceneItem.titleIcon),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(start = 80.dp),
                text = mainSceneItem.title,
                color = Color.White,
                fontSize = 24.sp,
            )
        }

        LazyRowWithIndicator(Modifier.width(894.dp), mainSceneItem, viewModel)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyRowWithIndicator(
    modifier: Modifier,
    mainSceneItem: MainSceneItem,
    viewModel: MainViewModel
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        ) {
            itemsIndexed(mainSceneItem.scenes) { index,scene ->
                when (scene.sceneType) {
                    is CameraScene -> SubCameraSceneView(index,scene,listState, viewModel)
                    is PicScene -> SubPicSceneView(index, scene, listState,viewModel)
                }
            }
        }

        // 指示器
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(0.dp, 581.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalItems = mainSceneItem.scenes.size
            val currentPageIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            for (i in 0 until totalItems) {
                Box(
                    modifier = Modifier
                        .size(if (i == currentPageIndex) 12.dp else 8.dp)  // 当前页指示器大一点
                        .background(
                            color = if (i == currentPageIndex) Color.White else Color.Gray,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}


@Preview(
    name = "Landscape Preview",
    widthDp = 1920,
    heightDp = 1080,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun HomeLeftContentPreview() {
    HomeLeftContent(DemoMainData, MainViewModel())

}