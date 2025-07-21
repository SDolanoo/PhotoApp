package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.DividerLine
import com.example.photoapp.features.faktura.composables.common.PriceSummary
import com.example.photoapp.features.faktura.composables.common.ProduktFakturaSection
import com.example.photoapp.features.faktura.composables.common.SectionCard
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura

@Composable
fun ProductReadOnly(
    modifier: Modifier,
    produkty: List<ProduktFaktura>
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Products",
            icon = Icons.Filled.ThumbUp
        ) {

            produkty.forEach { produkt ->
                ProduktFakturaSection(produkt)
                DividerLine()
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)

            PriceSummary(
                subtotal = "$1,345.50",
                tax = "$114.37",
                total = "$1,459.87"
            )
        }
    }
}