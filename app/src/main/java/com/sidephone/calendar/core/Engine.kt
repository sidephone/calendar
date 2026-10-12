package com.sidephone.calendar.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Engine {
	private val _output = MutableStateFlow("")
	val output: StateFlow<String> = _output

	fun onAction(action: Action) {
		when (action) {
			Action.POUND -> _output.value = "Pound key action"
			Action.STAR -> _output.value = "Star key action"
		}
	}
}
