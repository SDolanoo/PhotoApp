package com.example.photoapp.ui.FilterScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun PriceFilter(
    filterController: FilterController,
    onPriceRangeSelected: (Double?, Double?) -> Unit,
) {
    var selectedOption by filterController.priceSelectedOption

    var selectedRange by remember { mutableStateOf<PriceRange?>(null) }
    var customFromPrice by remember { mutableStateOf("") }
    var customToPrice by remember { mutableStateOf("") }
    val isCustomRangeValid = remember(customFromPrice, customToPrice) {
        validatePriceRange(customFromPrice, customToPrice)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (filterController.currentFilter.value == "faktura") {
            SegmentedButtonsPriceFilter { value -> //dataWystawienia or dataSprzedazy
                filterController.setCurrentFakturyPriceFilter(value)
            }
        }

        Text(
            text = "Kwota",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Predefined Price Ranges
        PriceRadioButtonGroup(
            options = listOf(
                PriceRange.UpTo100,
                PriceRange.From100To1000,
                PriceRange.Above1000
            ),
            selectedOption = selectedOption,
            onOptionSelected = {
                filterController.setPriceSelectedOption(it.id)
                customFromPrice = ""
                customToPrice = ""
                onPriceRangeSelected(it.from, it.to)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Price Range Inputs
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = customFromPrice,
                onValueChange = {
                    selectedRange = null
                    customFromPrice = it
                },
                label = { Text("Od") },
                isError = customFromPrice.isNotEmpty() && !isCustomRangeValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = customToPrice,
                onValueChange = {
                    selectedRange = null
                    customToPrice = it
                },
                label = { Text("Do") },
                isError = customToPrice.isNotEmpty() && !isCustomRangeValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        if (!isCustomRangeValid && (customFromPrice.isNotEmpty() || customToPrice.isNotEmpty())) {
            Text(
                text = "Tylko cyfry, np. 100.00",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PriceRadioButtonGroup(
    options: List<PriceRange>,
    selectedOption: String,
    onOptionSelected: (PriceRange) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selectedOption == option.id,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option.label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

enum class PriceRange(val id: String, val from: Double?, val to: Double?, val label: String) {
    UpTo100(id = "1", from = null, to = 100.0, label = "do 100,00"),
    From100To1000(id = "2", from = 100.0, to = 1000.0, label = "od 100,00 do 1000,00"),
    Above1000(id = "3", from = 1000.0, to = null, label = "powyżej 1000,00")
}

fun validatePriceRange(from: String, to: String): Boolean {
    val fromValue = from.toDoubleOrNull()
    val toValue = to.toDoubleOrNull()
    return when {
        fromValue == null && toValue == null -> false
        fromValue != null && toValue != null && fromValue > toValue -> false
        else -> true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonsPriceFilter(onChoice: (String) -> Unit){
    var selectedIndex by remember { mutableStateOf(1) }
    val options = listOf("brutto", "netto")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    val choice = options[index]
                    onChoice(choice)
                },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}