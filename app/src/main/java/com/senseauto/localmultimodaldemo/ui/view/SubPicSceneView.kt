package com.senseauto.localmultimodaldemo.ui.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.entity.SubSceneItem

@Composable
fun SubPicSceneView(
    index: Int,
    subSceneItem: SubSceneItem,
    listState: LazyListState,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val takePhotoAction by viewModel.takePhotoAction.collectAsState(false)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(subSceneItem) {
        snapshotFlow {
            listState.firstVisibleItemIndex == index
        }.collect { visible ->
            isVisible = visible
        }
    }

    LaunchedEffect(takePhotoAction) {
        if (takePhotoAction && isVisible) {
            viewModel.stopTakePhoto()
            viewModel.updateBitmap(BitmapFactory.decodeResource(context.resources, subSceneItem.sceneCover))
        }
    }

    Column {
        Image(
            modifier = Modifier
                .width(894.dp)
                .height(571.dp)
                .clip(RoundedCornerShape(20.dp)),
            painter = painterResource(subSceneItem.sceneCover),
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 30.dp)
                .width(894.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(subSceneItem.hints) {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .clickable {
                            viewModel.loadResult(
                                it,
                                BitmapFactory.decodeResource(
                                    context.resources,
                                    subSceneItem.sceneCover
                                )
                            )
                        }
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Gray.copy(alpha = 0.4F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 15.dp),
                        text = it.hint,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }

            }
        }
    }
}