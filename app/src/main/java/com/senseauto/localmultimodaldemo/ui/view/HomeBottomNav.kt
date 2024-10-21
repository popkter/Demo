package com.senseauto.localmultimodaldemo.ui.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.R
import com.senseauto.localmultimodaldemo.constant.ListData
import com.senseauto.localmultimodaldemo.entity.CameraScene
import com.senseauto.localmultimodaldemo.entity.HintItem
import com.senseauto.localmultimodaldemo.entity.MainSceneItem
import com.senseauto.localmultimodaldemo.entity.SubSceneItem

@Composable
fun HomeBottomNav(
    scenes: List<MainSceneItem>,
    selectIndex: Int,
    onIndexSelect: (Int) -> Unit,
) {
    val context = LocalContext.current
    LazyRow(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        itemsIndexed(scenes) { index, item ->
            Card(
                modifier = Modifier
                    .fillParentMaxHeight()
                    .aspectRatio(4.5f / 3f)
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onIndexSelect(index)
                    }
            ) {
                if (index == selectIndex) {
                    OverlayImages(
                        BitmapFactory.decodeResource(context.resources, item.naviIcon)
                            .asImageBitmap(),
                        BitmapFactory.decodeResource(context.resources, R.drawable.navi_icon_mask)
                            .asImageBitmap()
                    )
                } else {
                    Image(
                        painter = painterResource(item.naviIcon),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}