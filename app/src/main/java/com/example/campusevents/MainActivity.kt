package com.example.campusevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.campusevents.ui.app.CampusEventsApp
import com.example.campusevents.ui.theme.CampusEventsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusEventsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CampusEventsApp()
                }
            }
        }
    }
}
