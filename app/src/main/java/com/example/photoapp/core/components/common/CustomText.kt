package com.example.photoapp.core.components.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomText(
    title: String,
    field: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = field,
        modifier = modifier,
    )
}