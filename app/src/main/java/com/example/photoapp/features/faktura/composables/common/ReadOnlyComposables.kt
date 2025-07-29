package com.example.photoapp.features.faktura.composables.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFDDDDDD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF666666))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 18.sp, color = Color(0xFF111111), fontWeight = FontWeight.SemiBold)
            }
            content()
        }
    }
}

@Composable
fun TwoColumnRow(label1: String, value1: String, label2: String, value2: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            LabelText(label1)
            ValueText(value1)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            LabelText(label2)
            ValueText(value2)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        LabelText(label)
        ValueText(value)
    }
}

@Composable
fun LabelText(text: String) {
    Text(text = text, fontSize = 13.sp, color = Color(0xFF666666))
}

@Composable
fun ValueText(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color(0xFF111111), modifier = Modifier.padding(top = 4.dp))
}

@Composable
fun DividerLine() {
    Divider(
        color = Color(0xFFCCCCCC),
        thickness = 1.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun ProduktFakturaSection(produkt: ProduktFakturaZProduktem) {
    SectionCard(
        title = "Produkt",
        icon = Icons.Filled.ThumbUp
    ) {
        InfoRow("Nazwa Produktu", produkt.produkt.nazwaProduktu)
        TwoColumnRow("Ilość", produkt.produktFaktura.ilosc, "Jednostka Miary", produkt.produkt.jednostkaMiary)
        TwoColumnRow("Cena Netto", produkt.produkt.cenaNetto, "Stawka VAT", produkt.produkt.stawkaVat)
        TwoColumnRow("Wartość Netto", produkt.produktFaktura.wartoscNetto, "Wartość Brutto", produkt.produktFaktura.wartoscBrutto)
        InfoRow("Rabat", produkt.produktFaktura.rabat)
    }
}

@Composable
fun PriceSummary(subtotal: String, tax: String, total: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal:", fontSize = 14.sp, color = Color(0xFF666666))
            Text(subtotal, fontSize = 14.sp, color = Color(0xFF111111))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tax:", fontSize = 14.sp, color = Color(0xFF666666))
            Text(tax, fontSize = 14.sp, color = Color(0xFF111111))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total:", fontSize = 16.sp, color = Color(0xFF111111), fontWeight = FontWeight.SemiBold)
            Text(total, fontSize = 16.sp, color = Color(0xFF111111), fontWeight = FontWeight.SemiBold)
        }
    }
}
