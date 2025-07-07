package com.example.photoapp.features.faktura.composables

import android.R.attr.onClick
import android.R.attr.textColor
import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

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
    icon: ImageVector? = null,
    textColor: Color? =  null,
    outlineColor: Color? = null,
    height: Dp = 48.dp
) {
    // Domyślne kolory z MaterialTheme
    val defaultTextColor = MaterialTheme.colorScheme.onSurface
    val defaultOutlineColor = MaterialTheme.colorScheme.outline

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(height),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor ?: defaultTextColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = outlineColor ?: defaultOutlineColor
        )
    ) {
        // Przed tekstem odstęp
        Spacer(modifier = Modifier.width(1.dp))

        // Tekst z kolorem
        Text(
            text = title,
            color = textColor ?: defaultTextColor
        )

        // Ikona jeśli dostępna
        if (icon != null) {
            Spacer(modifier = Modifier.width(1.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor ?: defaultTextColor
            )
        }

        Spacer(modifier = Modifier.width(1.dp))
    }
}