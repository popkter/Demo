package com.senseauto.localmultimodaldemo.ui.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.senseauto.localmultimodaldemo.entity.IntentIdentifyScene
import com.senseauto.localmultimodaldemo.entity.MainSceneItem
import com.senseauto.localmultimodaldemo.entity.PicScene

@Composable
fun HomeLeftContent(mainSceneItem: MainSceneItem, viewModel: MainViewModel) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 20.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1F), verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .height(75.dp)
                    .padding(vertical = 10.dp),
                contentScale = ContentScale.Inside,
                painter = painterResource(mainSceneItem.titleIcon),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(start = 20.dp),
                text = mainSceneItem.desc,
                color = Color.White,
                fontSize = 20.sp,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(6F)
        ){
            LazyRowWithIndicator(mainSceneItem, viewModel)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyRowWithIndicator(
    mainSceneItem: MainSceneItem,
    viewModel: MainViewModel,
) {
    val pagerState = rememberPagerState {mainSceneItem.scenes.filter { it.sceneType == PicScene }.size }

    LaunchedEffect(pagerState) {
        pagerState.scrollToPage(0)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))) {
        if (mainSceneItem.sceneType == IntentIdentifyScene) {

            var selectedTabIndex by remember { mutableIntStateOf(0) }

            // Tab 组件的标题列表
            val tabs = listOf("行车中","下车后")

            when (selectedTabIndex) {
                0 -> {
                    HorizontalPager(
                        state = pagerState.apply { pageCount },
                        modifier = Modifier.fillMaxSize(),
                        pageSpacing = 20.dp,
                        beyondBoundsPageCount = 1
                    ) { index ->
                        val scene = mainSceneItem.scenes.filter { it.sceneType == PicScene }[index]
                        when (scene.sceneType) {
                            CameraScene -> SubCameraSceneView(scene, viewModel)
                            PicScene -> SubPicSceneView(scene, viewModel)
                        }
                    }

                    if (pagerState.pageCount > 1){
                        // 指示器
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(0.dp, (-80).dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(4) { iteration ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp, 4.dp)
                                        .background(
                                            color = if (iteration == pagerState.currentPage) Color.White else Color.Gray,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                )
                            }
                        }
                    }
                }
                1 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SubCameraSceneView(mainSceneItem.scenes.first { it.sceneType == CameraScene }, viewModel)
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
            ) {
                // 循环创建每个 Tab
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
        }else{
            val pagerState = rememberPagerState { mainSceneItem.scenes.size }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 20.dp,
                beyondBoundsPageCount = 1
            ) { index ->
                val scene = mainSceneItem.scenes[index]
                when (scene.sceneType) {
                    CameraScene -> SubCameraSceneView(scene, viewModel)
                    PicScene -> SubPicSceneView(scene, viewModel)
                }
            }

            if (pagerState.pageCount > 1){
                // 指示器
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(0.dp, (-80).dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        Box(
                            modifier = Modifier
                                .size(20.dp, 4.dp)
                                .background(
                                    color = if (iteration == pagerState.currentPage) Color.White else Color.Gray,
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }
                }
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
//    HomeLeftContent(DemoMainData, MainViewModel())
}