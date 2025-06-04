package com.example.photoapp.ui.FilterScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.photoapp.ui.FilterScreen.expandableSections.ExpandableSection
import java.util.Date

data class FilterResult(
    val filterType: String,
    val startDate: Date?,
    val endDate: Date?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val dateFilterOption: String? = null, // for Faktura only
    val priceFilterOption: String? = null // for Faktura only
)

sealed class FilterType(val id: String) {
    object Paragon : FilterType("paragon")
    object Faktura : FilterType("faktura")
    // Add more types here
}

// Main FilterScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenContent(
    filterController: FilterController,
    )
{
    val currentFilter by filterController.currentFilter

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SegmentedButtonsFilterChange(
            selectedFilter = currentFilter,
            onFilterChange = { new ->
                filterController.changeFilter(
                    when (new) {
                        "paragon" -> FilterType.Paragon
                        "faktura" -> FilterType.Faktura
                        else -> error("Unsupported filter type")
                    }.toString()
                )
            }
        )

        ExpandableSection(title = "Data") {
            DateFilter(
                filterController = filterController,
                onDateRangeSelected = { A, B -> filterController.setDateRange(Pair(A, B)) }
            )
        }

        ExpandableSection(title = "Cena") {
            PriceFilter(
                filterController = filterController,
                onPriceRangeSelected = { A, B -> filterController.setPriceRange(Pair(A, B)) }
            )
        }

        // Add other filter sections here (recipients, vendors, etc.)

        // Other filters (e.g., recipients, vendors, stores)
        Text("Other Filters (Recipients, Vendors, Stores) are placeholders.")
    }
}


@Composable
fun SegmentedButtonsFilterChange(selectedFilter: String, onFilterChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onFilterChange("paragon") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedFilter == "paragon") Color.Blue else Color.Gray
            )
        ) {
            Text("Paragon")
        }
        Button(
            onClick = { onFilterChange("faktura") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedFilter == "faktura") Color.Blue else Color.Gray
            )
        ) {
            Text("Faktura")
        }
    }
}

