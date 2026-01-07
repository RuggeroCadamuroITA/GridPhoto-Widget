package com.example.photowidget

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("GridPhoto Widget", fontSize = 24.sp)
                        Text("Open Source Project")
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RuggeroCadamuroITA")))
                        }) { Text("RuggeroCadamuroITA su GitHub") }
                    }
                }
            }
        }
    }
}