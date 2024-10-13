package com.popkter.dialpad.ui.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popkter.dialpad.ui.theme.DialPadTheme

val keys = listOf(
    DialKey("1", arrayListOf("∞")),
    DialKey("2", arrayListOf("A", "B", "C")),
    DialKey("3", arrayListOf("D", "E", "F")),
    DialKey("4", arrayListOf("G", "H", "I")),
    DialKey("5", arrayListOf("J", "K", "L")),
    DialKey("6", arrayListOf("M", "N", "O")),
    DialKey("7", arrayListOf("P", "Q", "R", "S")),
    DialKey("8", arrayListOf("T", "U", "V")),
    DialKey("9", arrayListOf("W", "X", "Y", "Z")),
    DialKey("*", arrayListOf("+", "ᛒ")),
    DialKey("0", arrayListOf("｜_｜")),
    DialKey("#", arrayListOf("-", "🎵")),

    )

@Composable
fun BottomDialPad(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        LazyVerticalGrid(
            GridCells.Fixed(3),
            modifier = Modifier.align(Alignment.Bottom),
            horizontalArrangement = Arrangement.spacedBy(2.dp),  // 设置水平间距
            verticalArrangement = Arrangement.spacedBy(2.dp)  // 设置垂直间距
        ) {
            items(keys) {
                DialKeyItemView(it)
            }
        }
    }
}

@Composable
fun DialKeyItemView(dialKey: DialKey) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .height(60.dp)
            .clickable {
//                Toast.makeText(context, dialKey.number, Toast.LENGTH_SHORT).show()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        contentColor = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(start = 10.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dialKey.number,
                color = Color.Black,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            LazyRow(modifier = Modifier.padding(top = 5.dp, start = 5.dp)) {
                items(dialKey.alphabets) {
                    Text(
                        text = it,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}

data class DialKey(
    val number: String = "",
    val alphabets: List<String> = emptyList()
)


@Preview
@Composable
fun BottomDialPadPreview() {
    DialPadTheme {
        BottomDialPad()
    }
}