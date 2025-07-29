package com.example.photoapp.features.selector.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.core.components.MyNavigationBar
import com.example.photoapp.core.navigation.NavBarDestinations
import com.example.photoapp.features.selector.presentation.odbiorca.OdbiorcaEditingScreen
import com.example.photoapp.features.selector.presentation.produkt.ProductsEditingScreen
import com.example.photoapp.features.selector.presentation.sprzedawca.SprzedawcaEditingScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorScreen(
    navController: NavHostController,
    goBack: () -> Unit,
    viewModel: SelectorViewModel = hiltViewModel()
) {
    viewModel.updateLists()

    val allProdukty = viewModel.allProdukty.collectAsState()
    val allOdbiorcy = viewModel.allOdbiorcy.collectAsState()
    val allSprzedawcy = viewModel.allSprzedawcy.collectAsState()

    var currentlyViewing by remember { mutableStateOf("main") }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (currentlyViewing == "main") "Wybierz do edycji" else currentlyViewing,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (currentlyViewing != "main") goBack() else currentlyViewing = "main" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(painter = painterResource(R.drawable.baseline_filter_list_alt_24), contentDescription = "Exit Filters")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            MyNavigationBar(
                navController = navController,
                destinations = NavBarDestinations.entries
            )
        }
    ) { innerPadding ->
        if (currentlyViewing == "main") {
            SelectorScreenContent(
                paddingValues = innerPadding,
                onSprzedawcyClick = {},
                onOdbiorcyClick = {},
                onProduktyClick = {},
            )
        }
        if (currentlyViewing == "Sprzedawcy") {
            SprzedawcaEditingScreen(
                sprzedawcy = allSprzedawcy.value
            )
        }
        if (currentlyViewing == "Odbiorcy") {
            OdbiorcaEditingScreen(
                odbiorcy = allOdbiorcy.value
            )
        }
        if (currentlyViewing == "Produkty") {
            ProductsEditingScreen(
                produkty = allProdukty.value
            )
        }
    }
}

@Composable
fun SelectorScreenContent(
    paddingValues: PaddingValues,
    onSprzedawcyClick: () -> Unit,
    onOdbiorcyClick: () -> Unit,
    onProduktyClick: () -> Unit,
) {
    Box(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            MenuButton(
                "Sprzedawcy",
                onClick = onSprzedawcyClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            MenuButton(
                "Odbiorcy",
                onClick = onOdbiorcyClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            MenuButton(
                "Produkty",
                onClick = onProduktyClick
            )
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        ),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
