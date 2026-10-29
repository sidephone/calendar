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
	// Base colors
	background  = Color(0xFF121212),
	surface = Color(0xFF2C2C2E),
	surfaceVariant = Color(0xFF38383A),

	// Text
	onBackground = Color(0xFFDEE3E5), // date numbers, week days
	onSurface = Color(0xFFF2F3F5),
	onSurfaceVariant = Color(0xFF6F6F77), // date numbers for days outside of the current month

	// Interactive elements, e.g. selected cell
	primary = Color(0xFF00796B),
	onPrimary = Color(0xFFFFFFFF),

	primaryContainer = Color(0xFF004E5A),
	onPrimaryContainer = Color(0xFFD0E8E9),
	onPrimaryFixed = Color(0xFFB8C7CB),  // event metadata

	// Calendar grid
	outline = Color(0xFF262B2D),

	// Event markers
	secondary = Color(0xFF26A69A), // event marker in the grid

	// Add-event button
	tertiary = Color(0xFF303637),
	onTertiary = Color(0xFFDEE3E5),


	// calendar grid
//	background = Color(0XFF121212),
//	onBackground = Color(0XFFDEE3E5), // date numbers, week days
//	onSurfaceVariant = Color(0XFF9CA3AF),  // date numbers for days outside of the current month
//
//	primary = Color(0XFFBDC5EB), // grid lines
//	onPrimary = Color(0XFF272F4D),
//
//	secondary = Color(0XFF004E5A), // event background
//	onSecondary = Color(0XFFA5EEFF), // event text
//
//	tertiary = Color(0XFF303637), // add new event background
//	onTertiary = Color(0XFFDEE3E5), // add new event text
)

val DisabledAlpha = 0.38f
