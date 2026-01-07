package com.example.photowidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : ComponentActivity() {
    private val photosState = mutableStateListOf<File>()
    private val queue = mutableListOf<Uri>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshPhotos()

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(title = { Text("GridPhoto Widget") },
                            actions = {
                                IconButton(onClick = { startActivity(Intent(this@MainActivity, InfoActivity::class.java)) }) {
                                    Icon(Icons.Default.Info, contentDescription = "Info")
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) { Icon(Icons.Default.Add, "Aggiungi") }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                        LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
                            items(photosState) { file ->
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.padding(4.dp).aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            queue.clear()
            queue.addAll(uris)
            processNext()
        }
    }

    private fun processNext() {
        if (queue.isNotEmpty()) {
            val uri = queue.removeAt(0)
            val folder = File(filesDir, "cropped").apply { if (!exists()) mkdirs() }
            val destUri = Uri.fromFile(File(folder, "img_${System.currentTimeMillis()}.jpg"))
            UCrop.of(uri, destUri).withAspectRatio(1f, 1f).withMaxResultSize(800, 800).start(this)
        } else {
            refreshPhotos()
            updateAllWidgets()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) processNext()
    }

    private fun refreshPhotos() {
        val folder = File(filesDir, "cropped")
        photosState.clear()
        photosState.addAll(folder.listFiles()?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList())
    }

    private fun updateAllWidgets() {
        val classes = listOf(PhotoWidget1x1::class.java, PhotoWidget2x2::class.java, PhotoWidget1x2::class.java)
        classes.forEach { cls ->
            val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, cls))
            val intent = Intent(this, cls).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            sendBroadcast(intent)
        }
    }
}