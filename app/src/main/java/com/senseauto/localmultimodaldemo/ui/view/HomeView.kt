package com.senseauto.localmultimodaldemo.ui.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.entity.MainSceneItem

@Composable
fun HomeView(modifier: Modifier, list: List<MainSceneItem>, viewModel: MainViewModel) {
    var index by remember { mutableIntStateOf(0) }
    Column(modifier = modifier
        .padding(horizontal = 40.dp)
        .padding(vertical = 40.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HomeLeftContent(list[index], viewModel)
            HomeRightContent(viewModel)
        }

        Spacer(Modifier.height(20.dp))

        HomeBottomNav(list) {
            index = it
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
fun HomeViewPreview() {
    HomeView(Modifier, DemoData, MainViewModel())
}