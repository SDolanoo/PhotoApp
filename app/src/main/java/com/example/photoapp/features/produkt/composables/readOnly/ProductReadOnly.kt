package com.example.photoapp.features.produkt.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.DividerLine
import com.example.photoapp.core.components.common.PriceSummary
import com.example.photoapp.core.components.common.ProduktFakturaSection
import com.example.photoapp.core.components.common.SectionCard
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem

@Composable
fun ProductReadOnly(
    modifier: Modifier,
    produkty: List<ProduktFakturaZProduktem>
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

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFDDDDDD))

            PriceSummary(
                subtotal = "$1,345.50",
                tax = "$114.37",
                total = "$1,459.87"
            )
        }
    }
}