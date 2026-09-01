package com.example.bluehourbooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bluehourbooks.ui.BlueHourApp
import com.example.bluehourbooks.ui.theme.BlueHourBooksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueHourBooksTheme {
                BlueHourApp()
            }
        }
    }
}
