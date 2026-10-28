package com.sidephone.calendar.core

import android.app.Application
import android.hardware.input.InputManager
import com.sidephone.calendar.Settings
import com.sidephone.calendar.core.events.CalendarEvents
import com.sidephone.calendar.db.Event
import com.sidephone.calendar.input.ActionPad
import com.sidephone.calendar.input.Keypad
import kotlinx.coroutines.flow.StateFlow

class Calendar(settings: Settings, app: Application, inputManager: InputManager) {
	private val db = CalendarEvents(app)
	private val engine = Engine(db)
	private val keypad = ActionPad(settings, inputManager)

	private var ignoreInput = false

	// output
	val date: StateFlow<Triple<Int, Int, Int>> = engine.dateFlow
	val events = engine.eventsFlow
	val grid = engine.gridFlow
	val tile = keypad.tile
	val tileOverride = keypad.layoutOverride

	// input
	var useTouch = false
	var onDeleteEvent: () -> Unit
		get() = engine.onDeleteEvent
		set(value) { engine.onDeleteEvent = value }
	var onEditEvent: (Long) -> Unit
		get() = engine.onEditEvent
		set(value) { engine.onEditEvent = value }

	// methods
	init {
		keypad.onAction = { action -> engine.onAction(action) }
		engine.keypad = keypad.tile
	}

	suspend fun getEvent(id: Long): Event? = db.getEvent(id)
	fun deleteEvent() = engine.deleteCurrent()
	fun destroy() { keypad.destroy() }
	fun listenForKeypadChange() = keypad.listenForChanges()
	fun ignoreInput() { ignoreInput = true }
	fun listenForInput() { ignoreInput = false }
	fun nextTile(): Keypad.Layout = keypad.nextTile()
	fun onAction(action: Action) = engine.onAction(action) // handle actions from touchscreen
	fun onKeyDown(keyCode: Int) = !ignoreInput && keypad.onKeyDown(keyCode) // handle actions on physical key down
	fun onKeyUp(keyCode: Int) = !ignoreInput && keypad.onKeyUp(keyCode) // handle actions on physical key up
	fun clickCell(x: Int, y: Int) = useTouch && engine.select(x, y) && engine.onAction(Action.OK)
	fun saveEvent(event: Event) = db.saveEvent(event)
	fun stopListeningForKeypadChange() = keypad.stopListening()
}
