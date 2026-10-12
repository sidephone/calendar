package com.sidephone.calendar.input

import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class Keypad(private val inputManager: InputManager) {
	enum class Layout { COMPACT_QWERTY, GAMEPAD, NONE, SUNDIAL, T9, T9_NO_DPAD, UNKNOWN }

	private val layouts = mapOf(
		"d9bf35cac6ea4aa8e3d2e56aaf6548022165ff41" to Layout.COMPACT_QWERTY, // gxa535_qwerty
		"f039f068d76b8ac29f9727f377ef44ee36f8876a" to Layout.GAMEPAD, // // gxa535_gamepad
		"b1d7968bf965b884f53acbd93afcef9c147f49bc" to Layout.SUNDIAL, // // gxa535_media
		"a2169ecfa473854b09588692a30fe13505a1336f" to Layout.T9, // gxa535_phone
		"4b53bdc1e69f8a01a615a2566ad4b81a26b0a972" to Layout.T9_NO_DPAD // gxa535_t9
	)

	private var changeListener: InputManager.InputDeviceListener? = null
	protected val layoutState = MutableStateFlow(Layout.NONE)
	val layout: StateFlow<Layout> = layoutState


	abstract fun onChange()


	init {
		layoutState.value = Layout.UNKNOWN
	}


	fun detect() {
		inputManager
			.inputDeviceIds
			.map(InputDevice::getDevice)
			.forEach { detect(it) }
	}


	protected open fun detect(device: InputDevice?) {
		val oldLayout = layoutState.value

		layoutState.value = Layout.NONE

		layoutState.value = if (
			device != null
			&& !device.isVirtual
			&& device.supportsSource(InputDevice.SOURCE_KEYBOARD)
		) {
			layouts[device.descriptor] ?: Layout.NONE
		} else {
			Layout.NONE
		}

		if (oldLayout != layoutState.value) {
			onChange()
		}
	}


	open fun listenForChanges() {
		detect()

		if (changeListener == null) {
			changeListener = object : InputManager.InputDeviceListener {
				override fun onInputDeviceAdded(deviceId: Int) { detect(InputDevice.getDevice(deviceId)) }
				override fun onInputDeviceRemoved(deviceId: Int) { layoutState.value = Layout.NONE; onChange() }
				override fun onInputDeviceChanged(deviceId: Int) { detect(InputDevice.getDevice(deviceId)) }
			}
		}

		inputManager.registerInputDeviceListener(changeListener, Handler(Looper.getMainLooper()))
	}


	open fun stopListening() {
		if (changeListener != null) {
			inputManager.unregisterInputDeviceListener(changeListener)
		}
	}
}
