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

		keyActions[KeyEvent.KEYCODE_STAR] = Action.STAR
		keyActions[KeyEvent.KEYCODE_POUND] = Action.POUND
	}


	private fun setSundialLayout() {
		keyActions.clear()

		// bottom corner keys
		keyActions[KeyEvent.KEYCODE_TAB] = Action.STAR
		keyActions[KeyEvent.KEYCODE_ENTER] = Action.POUND
	}
}
