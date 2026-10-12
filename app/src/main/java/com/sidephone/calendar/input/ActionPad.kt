package com.sidephone.calendar.input

import android.hardware.input.InputManager
import android.view.KeyEvent
import com.sidephone.calendar.core.Action


/**
 * Maps physical key codes to specific app actions, allowing for key presses or releases to trigger
 * corresponding actions within the app.
 */
class ActionPad(inputManager: InputManager) : Keypad(inputManager) {
	private val keyActions = mutableMapOf<Int, Action>() // empty = use touchscreen
	internal var onAction: (Action) -> Unit = { action -> onAction(action) }
	private var layoutOverride: Layout = Layout.UNKNOWN


	init {
		detect()
	}


	internal fun changeLayout(layout: Layout) {
		layoutOverride = layout
		onChange()
	}


	override fun onChange() {
		val layout = if (layoutOverride != Layout.UNKNOWN) layoutOverride else layoutState.value

		when (layout) {
			Layout.NONE -> keyActions.clear()
			Layout.SUNDIAL -> setSundialLayout()
			else -> setDefaultLayout()
		}
	}


	fun onKeyDown(keyCode: Int): Boolean {
		keyActions.getOrDefault(keyCode, null)?.let { action ->
			onAction(action)
			return true
		}

		return false
	}


	fun onKeyUp(keyCode: Int): Boolean {
		return keyActions.containsKey(keyCode)
	}


	private fun setDefaultLayout() {
		keyActions.clear()

		// Compact QWERTY
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
		keyActions[KeyEvent.KEYCODE_2] = Action.UP
		keyActions[KeyEvent.KEYCODE_8] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_4] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_6] = Action.RIGHT

		// T9 DPAD, Gamepad
		keyActions[KeyEvent.KEYCODE_DPAD_UP] = Action.UP
		keyActions[KeyEvent.KEYCODE_DPAD_DOWN] = Action.DOWN
		keyActions[KeyEvent.KEYCODE_DPAD_LEFT] = Action.LEFT
		keyActions[KeyEvent.KEYCODE_DPAD_RIGHT] = Action.RIGHT

		// Gamepad
		keyActions[KeyEvent.KEYCODE_BUTTON_X] = Action.ZOOM_OUT
		keyActions[KeyEvent.KEYCODE_BUTTON_Y] = Action.ZOOM_IN
		keyActions[KeyEvent.KEYCODE_BUTTON_A] = Action.PREVIOUS_PAGE
		keyActions[KeyEvent.KEYCODE_BUTTON_B] = Action.NEXT_PAGE
	}


	private fun setSundialLayout() {
		keyActions.clear()

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
