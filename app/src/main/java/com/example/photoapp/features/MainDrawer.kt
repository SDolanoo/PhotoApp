package com.example.photoapp.features

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dolan.photoapp.R

@Composable
fun MainDrawer(
    navigateToMakePhoto: () -> Unit,
    navigateToExport: () -> Unit,
    closeDrawer: () -> Unit,
    onSignout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
    ) {
        PhotoAppLogo(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )
        NavigationDrawerItem(
            label = { Text("Dodaj fakturę") },
            icon = { Icon(painterResource(R.drawable.baseline_photo_camera_24), null) },
            selected = false,
            onClick = { navigateToMakePhoto(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Eksportuj faktury") },
            icon = { Icon(painterResource(R.drawable.upload_file), null) },
            selected = false,
            onClick = { navigateToExport(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        // ➖ Spacer to push bottom item down
        Spacer(modifier = Modifier.weight(1f))

        NavigationDrawerItem(
            label = { Text("Wyloguj") },
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_logout_24), null) },
            selected = false,
            onClick = { onSignout() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun PhotoAppLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}