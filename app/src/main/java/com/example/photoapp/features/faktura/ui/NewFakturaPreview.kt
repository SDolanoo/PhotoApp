package com.example.photoapp.features.faktura.ui

import android.util.Log.v
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.features.faktura.composables.product.ProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFakturaPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDeleteMode = false

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isDeleteMode) "Usuń Faktury" else "Szczegóły Faktura",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isDeleteMode) {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Deletion")
                        }
                    } else {
                        IconButton(onClick = {  }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                actions = {
                    if (isDeleteMode) {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Done, contentDescription = "Confirm Deletion")
                        }
                    } else {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Delete, contentDescription = "Enable Delete Mode")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn (modifier = Modifier.padding(innerPadding)) {
            item {
                ProductForm(modifier = Modifier)
            }
        }
    }
}