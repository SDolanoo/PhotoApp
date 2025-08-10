package com.example.photoapp.features.produkt.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.DividerLine
import com.example.photoapp.core.components.common.PriceSummary
import com.example.photoapp.core.components.common.ProduktFakturaSection
import com.example.photoapp.core.components.common.SectionCard
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem

@Composable
fun ProductReadOnly(
    modifier: Modifier,
    produkty: List<ProduktFakturaZProduktem>,
    faktura: Faktura
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Produkty",
            icon = Icons.Default.ShoppingCart
        ) {

            produkty.forEach { produkt ->
                ProduktFakturaSection(produkt)
                DividerLine()
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFDDDDDD))

            PriceSummary(
                subtotal = faktura.razemNetto.toString(),
                tax = faktura.razemVAT.toString(),
                total = faktura.razemBrutto.toString()
            )
        }
    }
}

private fun calculateTotal() {

}