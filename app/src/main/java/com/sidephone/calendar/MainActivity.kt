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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sidephone.calendar.core.Calendar
import com.sidephone.calendar.input.Keypad
import com.sidephone.calendar.ui.screens.Screen
import com.sidephone.calendar.ui.screens.ScreenHelp
import com.sidephone.calendar.ui.screens.event.ScreenEvent
import com.sidephone.calendar.ui.screens.calendar.ScreenCalendar
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
				val snackbarHostState = remember { SnackbarHostState() }
				val scope = rememberCoroutineScope()
				val context = LocalContext.current

				Scaffold(
					modifier = Modifier.fillMaxSize(),
					snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
				) { innerPadding ->
					var screen by remember { mutableStateOf(Screen.CALENDAR) }
					var currentEvent by remember { mutableLongStateOf(0L) }
					calendar.onEditEvent = { event ->
						currentEvent = event
						screen = Screen.EVENT
					}

					var isDeleteEventShown by remember { mutableStateOf(false) }
					calendar.onDeleteEvent = { isDeleteEventShown = true }

					val keypadTile = calendar.tile.collectAsState().value
					val keypadOverride = calendar.tileOverride.collectAsState().value

					var isTouchOn by remember { mutableStateOf(false) }

					isTouchOn = keypadTile == Keypad.Layout.NONE || keypadTile == Keypad.Layout.UNKNOWN
					calendar.useTouch = isTouchOn

					when (screen) {
						Screen.HELP -> {
							calendar.ignoreInput()
							ScreenHelp(
								modifier = Modifier.padding(innerPadding),
								keypadTile = keypadTile,
								onBack = { screen = Screen.CALENDAR })
						}

						Screen.EVENT -> {
							calendar.ignoreInput()
							ScreenEvent(
								modifier = Modifier.padding(innerPadding),
								calendar = calendar,
								eventId = currentEvent,
								onBack = { screen = Screen.CALENDAR }
							)
						}

						Screen.CALENDAR -> {
							if (isDeleteEventShown) calendar.ignoreInput()
							else calendar.listenForInput()

							ScreenCalendar(
								modifier = Modifier.padding(innerPadding),
								snackbarProps = Triple(snackbarHostState, scope, context),
								calendar = calendar,
								isTouchOn = isTouchOn,
								isDeleteEventShown = isDeleteEventShown,
								onDeleteEventDismissed = { isDeleteEventShown = false },
								keypadOverride = keypadOverride,
								onHelp = { screen = Screen.HELP }
							)
						}
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
