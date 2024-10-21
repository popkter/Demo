package com.senseauto.localmultimodaldemo.ui.view

import android.graphics.BitmapFactory
import android.graphics.BlendMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.senseauto.localmultimodaldemo.R

@Composable
fun OverlayImages(image1: ImageBitmap, image2: ImageBitmap) {
    Box(modifier = Modifier.wrapContentSize().clip(RoundedCornerShape(10.dp))) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            // 将第一张图片绘制为背景
            drawImage(
                image = image1,
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )

            // 叠加第二张图片，使用 Shader 或者 BlendMode
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    blendMode = BlendMode.SCREEN
                }

                canvas.nativeCanvas.apply {
                    drawBitmap(image2.asAndroidBitmap(), 0f, 0f, paint)
                }
            }
        }
    }
}

@Composable
@Preview(
    device = "spec:width=1920dp,height=1080dp,dpi=160"
)
fun OverlayImagesPreview() {
    val context = LocalContext.current
    OverlayImages(
        BitmapFactory.decodeResource(context.resources, R.drawable.navi_icon_e_gf).asImageBitmap(),
        BitmapFactory.decodeResource(context.resources, R.drawable.navi_icon_mask).asImageBitmap()
    )
}
