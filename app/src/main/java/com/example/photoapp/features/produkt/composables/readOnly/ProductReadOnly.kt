package com.example.photoapp.features.produkt.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem

@Composable
fun ProductReadOnly(
    modifier: Modifier,
    produkty: List<ProduktFakturaZProduktem>
) {
    var subTotal by remember { mutableDoubleStateOf(0.0) }
    var tax by remember { mutableDoubleStateOf(0.0) }
    var total by remember { mutableDoubleStateOf(0.0) }

    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Products",
            icon = Icons.Filled.ThumbUp
        ) {

            produkty.forEach { produkt ->
                subTotal = subTotal + (produkt.produktFaktura.wartoscNetto.toDoubleOrNull() ?: 0.0)
                total = total + (produkt.produktFaktura.wartoscBrutto.toDoubleOrNull() ?: 0.0)
                tax = total - subTotal
                ProduktFakturaSection(produkt)
                DividerLine()
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFDDDDDD))

            PriceSummary(
                subtotal = subTotal.toString(),
                tax = tax.toString(),
                total = total.toString()
            )
        }
    }
}

private fun calculateTotal() {

}