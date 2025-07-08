package com.example.photoapp.features.faktura.composables.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

/**
 * A reusable, customizable outlined button with optional icon and color overrides.
 *
 * This composable wraps an `OutlinedButton`, allowing consumers to specify a title,
 * an optional icon, and optionally override the text and outline colors. If colors are
 * not provided, it falls back to theme defaults (`MaterialTheme.colorScheme`).
 *
 * This is useful when you want consistent button behavior with slight styling flexibility
 * across your app.
 *
 * Example usage:
 * ```
 * CustomOutlinedButton(
 *     title = "Submit",
 *     onClick = { submitForm() },
 *     icon = Icons.Default.Send,
 *     textColor = Color.White,
 *     outlineColor = Color.Blue
 * )
 * ```
 *
 * @param title The text label to display inside the button.
 * @param onClick Lambda triggered when the button is clicked.
 * @param icon Optional `ImageVector` to show next to the title text.
 * @param textColor Optional override for the text and icon color. Defaults to `onSurface`.
 * @param outlineColor Optional override for the button border color. Defaults to `outline`.
 * @param height The height of the button. Defaults to 48.dp.
 */


@Composable
fun CustomOutlinedButton(
    title: String,
    onClick: () -> Unit,
    icon: Painter? = null,
    textColor: Color? =  null,
    outlineColor: Color? = null,
    height: Int = 48,
    modifier: Modifier = Modifier
) {
    // Domyślne kolory z MaterialTheme
    val defaultTextColor = MaterialTheme.colorScheme.onSurface
    val defaultOutlineColor = MaterialTheme.colorScheme.outline

    val fontSize = when {
        height <= 28 -> 10.sp
        height <= 36 -> 12.sp
        height <= 48 -> 14.sp
        else -> 16.sp
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .defaultMinSize(minHeight = height.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor ?: defaultTextColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = outlineColor ?: defaultOutlineColor
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp) // ważne przy małych wysokościach
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = textColor ?: defaultTextColor,
                modifier = Modifier
                    .size(fontSize.value.dp + 16.dp)
                    .padding(end = 4.dp)
            )
        }

        Text(
            text = title,
            color = textColor ?: defaultTextColor,
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}