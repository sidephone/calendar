package com.sidephone.calendar

import android.hardware.input.InputManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.screens.ScreenHelp
import com.sidephone.calendar.ui.screens.ScreenMain
import com.sidephone.calendar.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
	private lateinit var calendar: Calendar

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		calendar = Calendar(
			Settings(this),
			application,
			getSystemService(INPUT_SERVICE) as InputManager
		)

		enableEdgeToEdge()
		setContent {
			AppTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					var isHelpScreenOn by remember { mutableStateOf(false) }

					val keypadTile = calendar.tile.collectAsState().value

					if (isHelpScreenOn) {
						ScreenHelp(modifier = Modifier.padding(innerPadding), keypadTile = keypadTile, onBack = { isHelpScreenOn = false })
					} else {
						ScreenMain(modifier = Modifier.padding(innerPadding), calendar = calendar, keypadTile = keypadTile, onHelp = { isHelpScreenOn = true })
					}
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		calendar.listenForKeypadChange()
	}

	override fun onPause() {
		super.onPause()
		calendar.stopListeningForKeypadChange()
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		return calendar.onKeyDown(keyCode) || super.onKeyDown(keyCode, event)
	}

	override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
		return calendar.onKeyUp(keyCode) || super.onKeyUp(keyCode, event)
	}

	override fun onDestroy() {
		super.onDestroy()
		calendar.destroy()
	}
}
