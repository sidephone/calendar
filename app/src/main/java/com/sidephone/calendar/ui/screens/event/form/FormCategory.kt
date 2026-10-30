package com.sidephone.calendar.ui.screens.event.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sidephone.calendar.ui.theme.Dimens

@Composable
fun FormCategory(titleResId: Int = 0) {
	Box {
		HorizontalDivider(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = Dimens.FORM_INPUT_SPACING),
			color = MaterialTheme.colorScheme.outline,
			thickness = 1.dp
		)

		if (titleResId == 0) return@Box

		Text(
			modifier = Modifier
				.padding(top = Dimens.FORM_INPUT_SPACING * 2)
				.padding(horizontal = Dimens.FORM_INPUT_SPACING),
			text = stringResource(titleResId),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.onBackground,
		)
	}
}
