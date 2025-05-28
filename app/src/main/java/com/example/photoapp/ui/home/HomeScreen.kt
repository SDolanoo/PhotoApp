package com.example.photoapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photoapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openDrawer: () -> Unit,
    navigateToCameraView: (String)-> Unit,
    navigateToParagonScreen: () -> Unit,
    navigateToFakturaScreen: () -> Unit,
    navigateToExcelPacker: () -> Unit,
    navigateToRaportFiskalnyScreen: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Photo App",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { openDrawer() }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                expanded = !expanded
//                navigateToCameraView()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

        },
    ) { innerPadding ->

            ScrollContent(
                innerPadding,
                navigateToParagonScreen,
                navigateToFakturaScreen,
                navigateToExcelPacker,
                navigateToRaportFiskalnyScreen)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add Paragon") },
                    onClick = { navigateToCameraView("paragon") }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Add Faktura") },
                    onClick = { navigateToCameraView("faktura") }
                )
            }
        }
    }
//}

@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    navigateToParagonScreen: () -> Unit,
    navigateToFakturaScreen: () -> Unit,
    navigateToExcelPacker: () -> Unit,
    navigateToRaportFiskalnyScreen: () -> Unit,
    ) {

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .paint(painter = painterResource(R.drawable.main_background_image),
                    contentScale = ContentScale.FillBounds),
            contentPadding = innerPadding,
            verticalItemSpacing = 2.dp,
            content = {
                item {
                    WelcomeCard(
                        /* TODO */
                    )
                }
                item {
                    MainCards(
                        navigateToParagonScreen = navigateToParagonScreen,
                        navigateToFakturaScreen = navigateToFakturaScreen,
                        navigateToExcelPacker = navigateToExcelPacker,
                        navigateToRaportFiskalnyScreen = navigateToRaportFiskalnyScreen,
                    )
                }
            }
        )
}

@Composable
fun WelcomeCard() {
    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .size(width = 360.dp, 120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome ___ \n" +
                            "Thanks for using the app",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }

        }
    }
}

@Composable
fun MainCards(
    navigateToParagonScreen: () -> Unit,
    navigateToFakturaScreen: () -> Unit,
    navigateToExcelPacker: () -> Unit,
    navigateToRaportFiskalnyScreen: () -> Unit
    ) {
    Column {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            // [START] PARAGONY
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 180.dp, height = 240.dp)
                    .padding(end = 5.dp),
                onClick = navigateToParagonScreen
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.paragon),
                        contentDescription = "android image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Paragony",
                        modifier = Modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // [END] PARAGONY

            // [START} FAKTURY
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 180.dp, height = 240.dp)
                    .padding(start = 5.dp),
                onClick = navigateToFakturaScreen
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.faktura),
                        contentDescription = "android image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Faktury",
                        modifier = Modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        // [END] FAKTURY

        // [START} STATISTICS
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 180.dp, height = 240.dp)
                    .padding(end = 5.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.piegraph),
                        contentDescription = "android image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Statystyki",
                        modifier = Modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // [END] STATISTICS

            // [START} EXCEL PACKER
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 180.dp, height = 240.dp)
                    .padding(start = 5.dp),
                onClick = navigateToExcelPacker
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.paragon),
                        contentDescription = "android image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "ExcelPacker",
                        modifier = Modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // [END] EXCEL PACKER
        }
        // [START] RAPORT FISKALNY
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 180.dp, height = 240.dp)
                    .padding(end = 5.dp),
                onClick = navigateToRaportFiskalnyScreen
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.raport_fiskalny_obraz),
                        contentDescription = "android image",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Raport fiskalny",
                        modifier = Modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // [END] RAPORT FISKALNY
        }
    }
}