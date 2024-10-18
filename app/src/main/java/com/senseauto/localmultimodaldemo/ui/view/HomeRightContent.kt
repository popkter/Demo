package com.senseauto.localmultimodaldemo.ui.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senseauto.localmultimodaldemo.Bot
import com.senseauto.localmultimodaldemo.MainViewModel

@Composable
fun HomeRightContent(viewModel: MainViewModel) {
    val list by viewModel.chatRecords.collectAsState(emptyList())
    val listState = rememberLazyListState()

    LaunchedEffect(list) {
        listState.animateScrollToItem(list.size)
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .padding(top = 75.dp)
                .size(894.dp, 571.dp),
            state = listState
        ) {
            items(list) { chatItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = if (chatItem.owner == Bot) Arrangement.Start else Arrangement.End // 根据 owner 对齐
                ) {
                    Column(
                        modifier = Modifier
                            .width(420.dp)
                            .wrapContentHeight()
                            .background(if (chatItem.owner == Bot) Color.Gray else Color.Blue)  // 可选：区分气泡颜色
                            .padding(8.dp)
                    ) {
                        Text(
                            text = chatItem.tts,
                            color = Color.White
                        )
                        chatItem.image?.let {
                            Image(
                                painter = BitmapPainter(it.asImageBitmap()),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(894.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                imageVector = Mic,
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .scale(2F)
                    .offset(-10.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .pointerInput(Unit){
                        detectTapGestures(
                            onLongPress = {
                                viewModel.startTakePhoto()
                                viewModel.startRecognize()
                            },
                        )
                    }
                    .background(Color.Cyan.copy(alpha = 0.4F)),
            )
        }
    }
}

