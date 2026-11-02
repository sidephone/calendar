package com.sidephone.calendar.ui.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
	modifier: Modifier = Modifier,
	accessibilityTitle: String,
	title: String = "",
	textIcon: String = "",
	imageIconResId: Int = 0,
	color: Color,
	highlightColor: Color = color,
	highlightBackground: Color = Color.Transparent,
	focusRequester: FocusRequester? = null,
	onPrev: (() -> Unit)? = null,
	onNext: (() -> Unit)? = null,
	onClick: () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		var focused by remember { mutableStateOf(false) }

		val focusReq =
			if (focusRequester == null) Modifier.focusProperties { canFocus = false }
			else Modifier
				.focusRequester(focusRequester)
				.focusProperties { canFocus = true }
				.onFocusChanged { focused = it.isFocused }


		IconButton(
			modifier = focusReq
				.padding(horizontal = 0.dp)
				.semantics { contentDescription = accessibilityTitle }
				.onPreviewKeyEvent { e ->
					if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

					when (e.key) {
						Key.DirectionLeft, Key.MediaPrevious -> {
							if (onPrev == null) false
							else { onPrev(); true }
						}

						Key.DirectionRight, Key.MediaNext -> {
							if (onNext == null) false
							else { onNext(); true }
						}

						else -> false
					}
				},
			onClick = { onClick() },
			colors = IconButtonDefaults.iconButtonColors(
				contentColor = if (focused) highlightColor else color,
				containerColor = if (focused) highlightBackground else Color.Transparent
			)
		) {
			if (imageIconResId != 0) {
				Icon(painterResource(id = imageIconResId), contentDescription = null)
			} else if (textIcon.isNotEmpty()) {
				Text(textIcon, color = color, style = MaterialTheme.typography.titleMedium)
			}
		}
		if (title.isNotEmpty()) Text(title, color = color, style = MaterialTheme.typography.labelLarge)
	}
}
