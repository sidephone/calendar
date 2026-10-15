package com.sidephone.calendar

import android.hardware.input.InputManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.ui.components.ChangeTileButton
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
					val txt = stringResource(R.string.app_name) +
						"\nactive tile: ${calendar.tile.collectAsState().value}" +
						"\noverride tile: ${calendar.tileOverride.collectAsState().value}" +
						"\nstate: ${calendar.grid.collectAsState().value}" +
						"\nevents: ${calendar.events.collectAsState().value}"

					Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
						ChangeTileButton(Modifier, calendar.tileOverride.collectAsState().value) { calendar.nextTile() }
						Text(text = txt)
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
