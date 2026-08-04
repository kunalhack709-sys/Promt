package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainAppBuilderScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppBuilderViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: AppBuilderViewModel = viewModel()
      val settings by viewModel.appSettings.collectAsState()

      MyApplicationTheme(darkTheme = settings.isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MainAppBuilderScreen(viewModel = viewModel)
        }
      }
    }
  }
}

