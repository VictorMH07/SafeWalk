package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.safewalk.ui.SafeWalkApp
import com.example.safewalk.ui.SafeWalkViewModel
import com.example.safewalk.ui.theme.SafeWalkTheme

class MainActivity : ComponentActivity() {
    private val vm: SafeWalkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeWalkTheme {
                SafeWalkApp(vm)
            }
        }
    }
}