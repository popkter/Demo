package com.senseauto.localmultimodaldemo.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.senseauto.localmultimodaldemo.MainViewModel
import com.senseauto.localmultimodaldemo.entity.SubSceneItem
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun SubCameraSceneView(
    index: Int,
    subSceneItem: SubSceneItem,
    listState: LazyListState,
    viewModel: MainViewModel
) {
    val TAG = "SubCameraSceneView"
    val context = LocalContext.current
    val takePhotoAction by viewModel.takePhotoAction.collectAsState(false)
    val coroutineScope = rememberCoroutineScope()
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
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
            capturePhoto(context, lensFacing, cameraExecutor) { bitmap ->
                capturedImage = bitmap
                viewModel.updateBitmap(bitmap)
                coroutineScope.launch {
                    previewView?.let {
                        startCamera(context, lensFacing, it)
                    }
                }
            }
        }
    }

    // 初始化 CameraX
    LaunchedEffect(previewView) {
        previewView?.let {
            startCamera(context, lensFacing, it)
        }
    }

    Column {
        // 相机预览视图
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }.also { previewView = it }
            },
            modifier = Modifier
                .width(894.dp)
                .height(571.dp)
                .clip(RoundedCornerShape(20.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                            coroutineScope.launch {
                                previewView?.let {
                                    startCamera(context, lensFacing, it)
                                }
                            }
                        }
                    )
                }
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
                            capturePhoto(context, lensFacing, cameraExecutor) { bitmap ->
                                capturedImage = bitmap
                                Log.e(TAG, "SubCameraSceneView: it= $it bitmap= $bitmap ")
                                viewModel.loadResult(it, bitmap)
                                coroutineScope.launch {
                                    previewView?.let {
                                        startCamera(context, lensFacing, it)
                                    }
                                }
                            }
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

 /*       // 显示捕获的图像
        capturedImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            )
        }*/
    }
}

@OptIn(ExperimentalCamera2Interop::class)
fun startCamera(context: Context, lensFacing: Int, previewView: PreviewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
    cameraProviderFuture.addListener({
        try {
            // 绑定相机生命周期
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (exc: Exception) {
            Log.e("CameraX", "Camera binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

fun capturePhoto(
    context: Context,
    lensFacing: Int,
    cameraExecutor: ExecutorService,
    onImageCaptured: (Bitmap) -> Unit
) {
    val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    cameraProviderFuture.addListener({
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                imageCapture
            )

            imageCapture.takePicture(

                cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.toBitmap()
                        onImageCaptured(bitmap.mirrorImage())
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraX", "Image capture failed", exception)
                    }
                }
            )

        } catch (exc: Exception) {
            Log.e("CameraX", "Camera binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

fun Bitmap.mirrorImage(): Bitmap {
    val matrix = Matrix().apply {
        preScale(-1f, 1f) // 水平翻转图像
    }
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}
