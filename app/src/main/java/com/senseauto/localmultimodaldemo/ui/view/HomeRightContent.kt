package com.senseauto.localmultimodaldemo.ui.view

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.senseauto.localmultimodaldemo.Bot
import com.senseauto.localmultimodaldemo.MainViewModel

private const val TAG = "HomeRightContent"

@Composable
fun HomeRightContent(viewModel: MainViewModel) {
    val list by viewModel.chatRecords.collectAsState(emptyList())
    val listState = rememberLazyListState()
    val voiceInputQuery by viewModel.voiceInputResult.collectAsState("")
    val chatResult by viewModel.currentResult.collectAsState("" to "")
    val isRecognizing by viewModel.isRecognizing.collectAsState(false)
    val voiceInputButtonBackground = if (isRecognizing) {
        Color.Green
    } else {
        Color.White
    }

    LaunchedEffect(list, chatResult) {
        listState.animateScrollToItem(list.size)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6.4F),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(list, key = { it.id }) { chatItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = if (chatItem.owner == Bot) Arrangement.Start else Arrangement.End // 根据 owner 对齐
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(0.dp, 420.dp)
                            .animateContentSize()
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                if (chatItem.owner == Bot) Color.White else Color.LightGray.copy(
                                    alpha = 0.75F
                                )
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (chatItem.id == chatResult.first) chatResult.second else chatItem.tts,
                            color = Color.Black,
                            fontSize = 24.sp,
                        )

                        chatItem.image?.let {
                            ImageWithDialog(
                                painter = BitmapPainter(it.asImageBitmap()),
                                contentDescription = "",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .weight(0.6F)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxHeight()
                    .weight(15F)
                    .align(Alignment.CenterVertically),
                text = voiceInputQuery,
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Image(
                imageVector = Mic,
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1F)
                    .weight(1F)
                    .clip(RoundedCornerShape(40.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                viewModel.startRecognize()
                                viewModel.startTakePhoto()
                            },
                        )
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                Log.e(TAG, "HomeRightContent: ${event.type}")
                                if (event.type == PointerEventType.Release) {
                                    viewModel.stopRecognize()
                                }
                            }
                        }
                    }
                    .background(voiceInputButtonBackground),
            )
        }
    }
}

@Composable
fun ImageWithDialog(
    painter: BitmapPainter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clickable { showDialog = true } // 点击时弹出Dialog
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.wrapContentSize()
        )
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(0.5F)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.sizeIn(0.dp, 0.dp, 1000.dp, 1000.dp)
                )
            }
        }
    }
}


@Preview(
    name = "Landscape Preview",
    widthDp = 1920,
    heightDp = 1080,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
fun GreetingPreview() {
    HomeRightContent(MainViewModel())
}
