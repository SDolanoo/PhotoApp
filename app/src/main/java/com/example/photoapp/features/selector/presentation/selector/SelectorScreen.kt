package com.example.photoapp.features.selector.presentation.selector

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
import com.dolan.photoapp.R
import com.example.photoapp.core.components.MyNavigationBar
import com.example.photoapp.core.navigation.NavBarDestinations
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.selector.presentation.selector.odbiorca.selector.OdbiorcaSelectorScreen
import com.example.photoapp.features.selector.presentation.selector.produkt.selector.ProductsSelectorScreen
import com.example.photoapp.features.selector.presentation.selector.sprzedawca.selector.SprzedawcaSelectorScreen
import com.example.photoapp.features.sprzedawca.data.Sprzedawca


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorScreen(
    navController: NavHostController,
    goToOdbiorcaDetails: (Odbiorca) -> Unit,
    goToSprzedawcaDetails: (Sprzedawca) -> Unit,
    goToProduktDetails: (Produkt) -> Unit,
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
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            if (currentlyViewing == "main") {
                MyNavigationBar(
                    navController = navController,
                    destinations = NavBarDestinations.entries
                )
            }
        }
    ) { innerPadding ->
        if (currentlyViewing == "main") {
            SelectorScreenContent(
                paddingValues = innerPadding,
                onSprzedawcyClick = { currentlyViewing = "Sprzedawcy" },
                onOdbiorcyClick = { currentlyViewing = "Odbiorcy" },
                onProduktyClick = { currentlyViewing = "Produkty" },
            )
        }
        if (currentlyViewing == "Sprzedawcy") {
            SprzedawcaSelectorScreen(
                sprzedawcy = allSprzedawcy.value,
                onClick = {goToSprzedawcaDetails(it)}
            )
        }
        if (currentlyViewing == "Odbiorcy") {
            OdbiorcaSelectorScreen(
                odbiorcy = allOdbiorcy.value,
                onClick = {goToOdbiorcaDetails(it)}
            )
        }
        if (currentlyViewing == "Produkty") {
            ProductsSelectorScreen(
                produkty = allProdukty.value,
                onClick = { goToProduktDetails(it) }
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
