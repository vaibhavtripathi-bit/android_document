package com.example.customviews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.customviews.ui.theme.CustomViewsTheme
import com.example.customviews.ui.catalog.CatalogApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomViewsTheme {
                CatalogApp()
            }
        }
    }
}
