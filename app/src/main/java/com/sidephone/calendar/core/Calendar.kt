package com.sidephone.calendar.core

import android.app.Application
import android.hardware.input.InputManager
import com.sidephone.calendar.Settings
import com.sidephone.calendar.input.ActionPad
import com.sidephone.calendar.input.Keypad
import kotlinx.coroutines.flow.StateFlow

class Calendar(settings: Settings, app: Application, inputManager: InputManager) {
	private val keypad = ActionPad(settings, inputManager)
	private val engine = Engine(app)

	val date: StateFlow<Triple<Int, Int, Int>> = engine.dateFlow
	val events = engine.eventsFlow
	val grid = engine.gridFlow
	val tile = keypad.tile
	val tileOverride = keypad.layoutOverride

	init { keypad.onAction = { action -> engine.onAction(action) } }

	fun destroy() { keypad.destroy() }
	fun listenForKeypadChange() = keypad.listenForChanges()
	fun nextTile(): Keypad.Layout = keypad.nextTile()
	fun onAction(action: Action) = engine.onAction(action) // handle actions from touchscreen
	fun onKeyDown(keyCode: Int) = keypad.onKeyDown(keyCode) // handle actions on physical key down
	fun onKeyUp(keyCode: Int) = keypad.onKeyUp(keyCode) // handle actions on physical key up
	fun stopListeningForKeypadChange() = keypad.stopListening()
}
