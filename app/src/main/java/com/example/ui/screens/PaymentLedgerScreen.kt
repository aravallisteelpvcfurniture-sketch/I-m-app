package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentRecord
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentLedgerScreen(
  viewModel: MainViewModel
) {
  val payments by viewModel.payments.collectAsState()
  val orders by viewModel.orders.collectAsState()

  var showAddDialog by remember { mutableStateOf(false) }
  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  val totalPaid = payments.sumOf { it.amount } + orders.sumOf { it.advancePaid }
  val totalOutstanding = orders.sumOf { it.remainingBalance }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Text(
        text = "Payment Ledger & Receipts",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Real-time tracking of advances, UPI/NEFT receipts and pending balances",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Totals Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MetallicGold, CharcoalBorder)))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TOTAL COLLECTED", style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
            Text(
              text = inrFormatter.format(totalPaid),
              style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("OUTSTANDING DUE", style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
            Text(
              text = inrFormatter.format(totalOutstanding),
              style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFFF8A65), fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (payments.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Payment, contentDescription = null, tint = SteelSilver, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No payments recorded yet", style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
            ) {
              Text("Record First Payment")
            }
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(payments) { p ->
            PaymentRow(p, inrFormatter)
          }
        }
      }
    }

    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = MetallicGold,
      contentColor = CharcoalDark,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("fab_record_payment")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Record Payment")
    }
  }

  if (showAddDialog) {
    RecordPaymentDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { orderNum, customer, amount, mode, ref, notes ->
        viewModel.recordPayment(orderNum, customer, amount, mode, ref, notes) {
          showAddDialog = false
        }
      }
    )
  }
}

@Composable
private fun PaymentRow(payment: PaymentRecord, inrFormatter: NumberFormat) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalCard)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = payment.customerName,
          style = MaterialTheme.typography.bodyLarge.copy(color = OffWhite, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Order: ${payment.orderNumber} • ${payment.paymentMode}",
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
        )
        if (payment.transactionRef.isNotBlank()) {
          Text(
            text = "Ref: ${payment.transactionRef}",
            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold.copy(alpha = 0.8f))
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF2E7D32).copy(alpha = 0.2f))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = inrFormatter.format(payment.amount),
          style = MaterialTheme.typography.titleMedium.copy(
            color = Color(0xFF81C784),
            fontWeight = FontWeight.Bold
          )
        )
      }
    }
  }
}

@Composable
private fun RecordPaymentDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, Double, String, String, String) -> Unit
) {
  var customerName by remember { mutableStateOf("") }
  var orderNumber by remember { mutableStateOf("ORD-2610") }
  var amountStr by remember { mutableStateOf("10000") }
  var paymentMode by remember { mutableStateOf("UPI") }
  var ref by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalCard,
    title = {
      Text("Record Payment Receipt", color = MetallicGold, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Customer Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = orderNumber,
          onValueChange = { orderNumber = it },
          label = { Text("Order / Invoice Number") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("Amount Received (₹) *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = paymentMode,
          onValueChange = { paymentMode = it },
          label = { Text("Payment Mode (UPI / CASH / CHEQUE / BANK)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = ref,
          onValueChange = { ref = it },
          label = { Text("Transaction UTR / Cheque Ref #") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Remarks (e.g. 50% advance)") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountStr.toDoubleOrNull() ?: 0.0
          if (customerName.isNotBlank() && amt > 0) {
            onConfirm(orderNumber, customerName, amt, paymentMode, ref, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
      ) {
        Text("Save Receipt")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SteelSilver)
      }
    }
  )
}
