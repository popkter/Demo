package com.senseauto.localmultimodaldemo.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senseauto.localmultimodaldemo.R
import com.senseauto.localmultimodaldemo.entity.CameraScene
import com.senseauto.localmultimodaldemo.entity.HintItem
import com.senseauto.localmultimodaldemo.entity.MainSceneItem
import com.senseauto.localmultimodaldemo.entity.SubSceneItem

@Composable
fun HomeBottomNav(scenes: List<MainSceneItem>, onIndexSelect: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        itemsIndexed(scenes) { index, item ->
            Card(
                modifier = Modifier
                    .width(360.dp)
                    .fillParentMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onIndexSelect(index)
                    }
            ) {
                Image(
                    painter = painterResource(item.naviIcon),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

val DemoMainData = MainSceneItem(
    naviIcon = R.drawable.scene_cover,
    titleIcon = R.drawable.ic_launcher_foreground,
    title = "场景识别",
    scenes = listOf(
        SubSceneItem(
            R.drawable.scene_cover,
            listOf(
                HintItem(
                    "这是什么",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                )
            )
        ),
        SubSceneItem(
            R.drawable.scene_cover,
            listOf(
                HintItem(
                    "这是什么",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                ),
                HintItem(
                    "介绍一下照片里的内容",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                )
            ),
            sceneType = CameraScene
        ),
        SubSceneItem(
            R.drawable.scene_cover,
            listOf(
                HintItem(
                    "这是什么",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                ),
                HintItem(
                    "介绍一下照片里的内容",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                )
            )
        ),
        SubSceneItem(
            R.drawable.scene_cover,
            listOf(
                HintItem(
                    "这是什么",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                ),
                HintItem(
                    "介绍一下照片里的内容1",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                ),
                HintItem(
                    "介绍一下照片里的内容2",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                ),
                HintItem(
                    "介绍一下照片里的内容3",
                    "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                )
            )
        )
    )
)

val DemoData = listOf(
    DemoMainData,
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        )
    ),
    MainSceneItem(
        naviIcon = R.drawable.scene_cover,
        titleIcon = R.drawable.ic_launcher_foreground,
        title = "场景识别",
        scenes = listOf(
            SubSceneItem(
                R.drawable.scene_cover,
                listOf(
                    HintItem(
                        "这是什么",
                        "你是一个帮助人们识别物体的人工智能，请帮我看一下图片中是什么物体"
                    )
                )
            )
        ),
    )
)



@Preview
@Composable
fun HomeBottomNavPreview() {
    HomeBottomNav(
        DemoData
    ) {}
}