package com.sidephone.calendar.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


val LightColorScheme = lightColorScheme(

//	background = Color(0XFFF5FAFC),
//	onBackground = Color(0XFF171D1E),
//	onSurfaceVariant = Color(0XFF4B5563),
//
//	primary = Color(0XFF555D7E), // grid lines
//	onPrimary = Color(0XFFFFFFFF),
//
//	secondary = Color(0XFFA5EEFF),
//	onSecondary = Color(0XFF001F25),
//
//	tertiary = Color(0XFFDEE3E5),
//	onTertiary = Color(0XFF171D1E),
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

	// unused
	surfaceVariant = Color(0xFF38383A),
)

const val DISABLED_ALPHA = 0.38f
