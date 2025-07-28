package com.example.photoapp.ui.ObjectsEditingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class InvoiceData(
    val id: String,
    val company: String,
    val amount: String,
    val date: String,
    val status: InvoiceStatus
)

enum class InvoiceStatus {
    Paid, Pending, Overdue
}

val sampleInvoices = listOf(
    InvoiceData("INV-001", "Acme Corporation", "$1,250.00", "Jan 15, 2025", InvoiceStatus.Paid),
    InvoiceData("INV-002", "Tech Solutions Ltd", "$850.75", "Jan 20, 2025", InvoiceStatus.Pending),
    InvoiceData("INV-003", "Global Services Inc", "$2,100.00", "Jan 10, 2025", InvoiceStatus.Overdue),
    InvoiceData("INV-004", "Digital Marketing Co", "$675.50", "Jan 18, 2025", InvoiceStatus.Paid),
    InvoiceData("INV-005", "Creative Studio", "$1,450.25", "Jan 22, 2025", InvoiceStatus.Pending),
    InvoiceData("INV-006", "Consulting Group", "$3,200.00", "Jan 25, 2025", InvoiceStatus.Paid)
)

@Composable
fun InvoicesScreen(invoices: List<InvoiceData>) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search invoices...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invoices) { invoice ->
                InvoiceCard(invoice = invoice)
            }
        }
    }
}

@Composable
fun InvoiceCard(invoice: InvoiceData, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

            Text(text = "Invoice #${invoice.id}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = invoice.company, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = invoice.amount, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = invoice.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                InvoiceStatusTag(status = invoice.status)
            }
        }
    }
}

@Composable
fun InvoiceStatusTag(status: InvoiceStatus) {
    val backgroundColor = when (status) {
        InvoiceStatus.Paid -> Color(0xFFDFF7E4)
        InvoiceStatus.Pending -> Color(0xFFEFF0FB)
        InvoiceStatus.Overdue -> Color(0xFFFCE8E8)
    }

    val textColor = when (status) {
        InvoiceStatus.Paid -> Color(0xFF2E7D32)
        InvoiceStatus.Pending -> Color(0xFF3F51B5)
        InvoiceStatus.Overdue -> Color(0xFFD32F2F)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


