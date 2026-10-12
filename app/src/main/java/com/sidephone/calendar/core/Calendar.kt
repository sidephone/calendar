package com.sidephone.calendar.core

import android.hardware.input.InputManager
import com.sidephone.calendar.Settings
import com.sidephone.calendar.input.ActionPad
import com.sidephone.calendar.input.Keypad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class Calendar(private val settings: Settings, inputManager: InputManager) {
	private val keypad = ActionPad(inputManager)
	private val engine = Engine()
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

	val output = engine.output

	private val _layoutOverride = MutableStateFlow(settings.getScreenLayout())
	val layoutOverride: StateFlow<Keypad.Layout> = _layoutOverride.asStateFlow()

	val layout: StateFlow<Keypad.Layout> = combine(keypad.layout, _layoutOverride) { detected, override ->
		if (override != Keypad.Layout.UNKNOWN) override else detected
	}.stateIn(
		scope,
		SharingStarted.Eagerly,
		if (_layoutOverride.value != Keypad.Layout.UNKNOWN) _layoutOverride.value else keypad.layout.value
	)


	init {
		keypad.onAction = { action -> engine.onAction(action) }
		keypad.changeLayout(settings.getScreenLayout())
	}

	fun destroy() { scope.cancel() }
	fun listenForKeypadChange() = keypad.listenForChanges()
	fun onAction(action: Action) = engine.onAction(action) // handle actions from touchscreen
	fun onKeyDown(keyCode: Int) = keypad.onKeyDown(keyCode) // handle actions on physical key down
	fun onKeyUp(keyCode: Int) = keypad.onKeyUp(keyCode) // handle actions on physical key up
	fun stopListeningForKeypadChange() = keypad.stopListening()


	/**
	 * Allow the user to switch between the possible app input methods (layouts) in a cycle. Useful for
	 * when keypad detection blips and forces the wrong tile.
	 */
	fun nextLayout() {
		val preferred = when (_layoutOverride.value) {
			// in this app the options are "none" (touchscreen), "sundial", or "any other tile"
			Keypad.Layout.UNKNOWN -> Keypad.Layout.T9
			Keypad.Layout.T9, Keypad.Layout.T9_NO_DPAD, Keypad.Layout.COMPACT_QWERTY, Keypad.Layout.GAMEPAD -> Keypad.Layout.SUNDIAL
			Keypad.Layout.SUNDIAL -> Keypad.Layout.NONE
			else -> Keypad.Layout.UNKNOWN
		}

		_layoutOverride.value = preferred
		keypad.changeLayout(preferred)
		settings.setScreenLayout(preferred)
	}
}
