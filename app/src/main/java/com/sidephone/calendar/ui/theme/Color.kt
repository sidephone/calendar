package com.sidephone.calendar.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


val LightColorScheme = lightColorScheme(
	// Grid
	background = Color(0xFFFAFAFA),
	outline = Color(0xFFE0E3E3), // borders
	secondary = Color(0xFF009A8C), // event marker
	onBackground = Color(0xFF252A2C), // date numbers, weekdays
	onSurfaceVariant = Color(0xFF92979A), // dates outside current month

	// Interactive elements, e.g. selected cell, selected button
	primary = Color(0xFF008A7A), // background
	onPrimary = Color(0xFFFFFFFF), // text

	// Event list
	primaryContainer = Color(0xFFD5EFEB), // event background
	onPrimaryContainer = Color(0xFF164E49), // event title
	onPrimaryFixed = Color(0xFF47736E), // event metadata
	tertiary = Color(0xFFE8E9EB), // add-event background
	onTertiary = Color(0xFF303438), // add-event text

	// Popup
	surface = Color(0xFFFFFFFF), // popup background
	onSurface = Color(0xFF202426), // popup title text
	onSecondaryContainer = Color(0xFF4D5558), // popup body text
)

val DarkColorScheme = darkColorScheme(
	// Grid
	background  = Color(0xFF121212),
	outline = Color(0xFF262B2D), // borders
	secondary = Color(0xFF26A69A), // event marker
	onBackground = Color(0xFFDEE3E5), // date numbers, week days
	onSurfaceVariant = Color(0xFF6F6F77), // date numbers outside the current month

	// interactive elements, e.g. selected cell, selected button
	primary = Color(0xFF00796B), // background
	onPrimary = Color(0xFFFFFFFF), // text

	// event list
	primaryContainer = Color(0xFF004E5A), // event background
	onPrimaryContainer = Color(0xFFD0E8E9), // event text
	onPrimaryFixed = Color(0xFFA5C3C8),  // event metadata
	tertiary = Color(0xFF37393E), // add-event background
	onTertiary = Color(0xFFDEE3E5), // add-event text

	// popup
	surface = Color(0xFF2C2C2E), // popup background
	onSurface = Color(0xFFF2F3F5), // popup title text
	onSecondaryContainer = Color(0xFFD8D9DA), // popup body text
)

const val DISABLED_ALPHA = 0.38f
