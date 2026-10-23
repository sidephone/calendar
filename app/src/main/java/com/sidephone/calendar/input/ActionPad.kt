package com.sidephone.calendar.input

import android.hardware.input.InputManager
import android.view.KeyEvent
import com.sidephone.calendar.Settings
import com.sidephone.calendar.core.Action
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


/**
 * 1. Maps physical key codes to specific app actions, allowing for key presses or releases to trigger
 * corresponding actions within the app.
 * 2. Allows overriding the automatic tile detection with a user-specified layout for the cases
 * where the automatic detection is incorrect or the user prefers a different layout (for example,
 * they want to enforce touchscreen even when a tile is connected).
 */
class ActionPad(private val settings: Settings, inputManager: InputManager) : Keypad(inputManager) {
	private val keyActions = mutableMapOf<Int, Action>() // empty = use touchscreen
	private val keyHandled = mutableMapOf<Int, Boolean>()
	internal var onAction: (Action) -> Boolean = { false }

	private val tileOverride = MutableStateFlow(settings.getTile())
	val layoutOverride: StateFlow<Layout> = tileOverride.asStateFlow()

	private val tileWatcher = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	val tile: StateFlow<Layout> = combine(layout, tileOverride) { detected, override ->
		if (override != Layout.UNKNOWN) override else detected
	}.stateIn(
		tileWatcher,
		SharingStarted.Eagerly,
		if (tileOverride.value != Layout.UNKNOWN) tileOverride.value else layout.value
	)


	init {
		detect()
		tileOverride.value = settings.getTile()
		onChange()
	}


	fun destroy() {
		tileWatcher.cancel()
	}


	/**
	 * Allow the user to switch between the possible app input methods (layouts) in a cycle. Useful for
	 * when keypad detection blips and forces the wrong tile.
	 */
	fun nextTile(): Layout {
		val override = when (tileOverride.value) {
			// in this app the options are "none" (touchscreen), "sundial", or "any other tile"
			Layout.UNKNOWN -> Layout.T9
			Layout.T9, Layout.T9_NO_DPAD, Layout.COMPACT_QWERTY, Layout.GAMEPAD -> Layout.SUNDIAL
			Layout.SUNDIAL -> Layout.NONE
			else -> Layout.UNKNOWN
		}

		tileOverride.value = override
		settings.setTile(override)
		onChange()

		return override
	}


	override fun onChange() {
		val selectedLayout = if (tileOverride.value != Layout.UNKNOWN) tileOverride.value else this.layout.value

		when (selectedLayout) {
			Layout.NONE -> keyActions.clear()
			Layout.SUNDIAL -> setSundialLayout()
			else -> setDefaultLayout()
		}
	}


	fun onKeyDown(keyCode: Int): Boolean {
		keyActions[keyCode]?.let { action ->
			keyHandled[keyCode] = onAction(action)
		}

		return keyHandled[keyCode] ?: false
	}


	fun onKeyUp(keyCode: Int): Boolean {
		val result = keyHandled[keyCode] ?: false
		keyHandled[keyCode] = false
		return result
	}


	private fun setDefaultLayout() {
		keyActions.clear()

		// Compact QWERTY
		keyActions[KeyEvent.KEYCODE_BACK] = Action.BACK
		keyActions[KeyEvent.KEYCODE_ENTER] = Action.OK
		keyActions[KeyEvent.KEYCODE_E] = Action.ZOOM_OUT
		keyActions[KeyEvent.KEYCODE_U] = Action.ZOOM_IN
		keyActions[KeyEvent.KEYCODE_ALT_LEFT] = Action.PREVIOUS_PAGE
		keyActions[KeyEvent.KEYCODE_SHIFT_LEFT] = Action.NEXT_PAGE

		keyActions[KeyEvent.KEYCODE_T] = Action.UP
		keyActions[KeyEvent.KEYCODE_B] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_D] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_J] = Action.RIGHT

		// T9 common
		keyActions[KeyEvent.KEYCODE_1] = Action.ZOOM_OUT
		keyActions[KeyEvent.KEYCODE_3] = Action.ZOOM_IN
		keyActions[KeyEvent.KEYCODE_STAR] = Action.PREVIOUS_PAGE
		keyActions[KeyEvent.KEYCODE_POUND] = Action.NEXT_PAGE

		// T9 no DPAD
		keyActions[KeyEvent.KEYCODE_5] = Action.OK
		keyActions[KeyEvent.KEYCODE_2] = Action.UP
		keyActions[KeyEvent.KEYCODE_8] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_4] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_6] = Action.RIGHT

		// T9 DPAD
		keyActions[KeyEvent.KEYCODE_DPAD_CENTER] = Action.OK

		// T9 DPAD, Gamepad
		keyActions[KeyEvent.KEYCODE_DPAD_UP] = Action.UP
		keyActions[KeyEvent.KEYCODE_DPAD_DOWN] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_DPAD_LEFT] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_DPAD_RIGHT] = Action.RIGHT

		// Gamepad
		keyActions[KeyEvent.KEYCODE_BUTTON_START] = Action.OK
		keyActions[KeyEvent.KEYCODE_BUTTON_X] = Action.ZOOM_OUT
		keyActions[KeyEvent.KEYCODE_BUTTON_Y] = Action.ZOOM_IN
		keyActions[KeyEvent.KEYCODE_BUTTON_A] = Action.PREVIOUS_PAGE
		keyActions[KeyEvent.KEYCODE_BUTTON_B] = Action.NEXT_PAGE
	}


	private fun setSundialLayout() {
		keyActions.clear()

		keyActions[KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE] = Action.OK
		keyActions[KeyEvent.KEYCODE_DPAD_LEFT] = Action.ZOOM_OUT
		keyActions[KeyEvent.KEYCODE_DPAD_RIGHT] = Action.ZOOM_IN
		keyActions[KeyEvent.KEYCODE_TAB] = Action.PREVIOUS_PAGE
		keyActions[KeyEvent.KEYCODE_ENTER] = Action.NEXT_PAGE

		keyActions[KeyEvent.KEYCODE_DPAD_UP] = Action.UP
		keyActions[KeyEvent.KEYCODE_DPAD_DOWN] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_MEDIA_PREVIOUS] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_MEDIA_NEXT] = Action.RIGHT
	}
}
