package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SiteMeasurement
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PdfInvoiceGenerator

@Composable
fun MeasurementScreen(
  viewModel: MainViewModel,
  onGenerateEstimateFromMeasurement: (customerName: String, phone: String, site: String, category: String) -> Unit
) {
  val context = LocalContext.current
  val measurements by viewModel.measurements.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }

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
        text = "Site Measurements Log",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Field measurements for Railings, Balconies, PVC Wardrobes & Kitchens",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Spacer(modifier = Modifier.height(16.dp))

      if (measurements.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.SquareFoot,
              contentDescription = null,
              tint = SteelSilver,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No site measurements recorded yet",
              style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
            ) {
              Text("Take First Measurement")
            }
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(measurements) { item ->
            MeasurementCard(
              measurement = item,
              onCall = { PdfInvoiceGenerator.makePhoneCall(context, item.customerPhone) },
              onWhatsApp = {
                PdfInvoiceGenerator.openWhatsAppChat(
                  context,
                  item.customerPhone,
                  "Namaste ${item.customerName}, site measurement for your ${item.category} has been logged: ${item.length}x${item.width} ${item.unit}."
                )
              },
              onGenerateEstimate = {
                onGenerateEstimateFromMeasurement(
                  item.customerName,
                  item.customerPhone,
                  item.siteAddress,
                  item.category
                )
              }
            )
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
        .testTag("fab_add_measurement")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Measurement")
    }
  }

  if (showAddDialog) {
    AddMeasurementDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, phone, address, category, l, w, h, qty, unit, notes ->
        viewModel.addMeasurement(
          customerName = name,
          customerPhone = phone,
          siteAddress = address,
          category = category,
          length = l,
          width = w,
          height = h,
          quantity = qty,
          unit = unit,
          notes = notes,
          onSuccess = { showAddDialog = false }
        )
      }
    )
  }
}

@Composable
private fun MeasurementCard(
  measurement: SiteMeasurement,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit,
  onGenerateEstimate: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, Color.Transparent)))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = measurement.customerName,
            style = MaterialTheme.typography.titleMedium.copy(
              color = OffWhite,
              fontWeight = FontWeight.Bold
            )
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = measurement.siteAddress,
              style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MetallicGold.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = measurement.category,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MetallicGold,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Dimensions Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CharcoalSurface)
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          DimensionItem("Length", "${measurement.length} ${measurement.unit}")
          DimensionItem("Width", "${measurement.width} ${measurement.unit}")
          if (measurement.height > 0) {
            DimensionItem("Height", "${measurement.height} ${measurement.unit}")
          }
          DimensionItem("Qty", "${measurement.quantity.toInt()} Nos")
        }
      }

      if (measurement.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Notes: ${measurement.notes}",
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (measurement.customerPhone.isNotBlank()) {
            IconButton(
              onClick = onCall,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
            ) {
              Icon(Icons.Default.Call, contentDescription = "Call", tint = MetallicGold, modifier = Modifier.size(18.dp))
            }
            IconButton(
              onClick = onWhatsApp,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
            ) {
              Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
            }
          }
        }

        Button(
          onClick = onGenerateEstimate,
          colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Make Quote")
        }
      }
    }
  }
}

@Composable
private fun DimensionItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(label, style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
    Text(value, style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold))
  }
}

@Composable
private fun AddMeasurementDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, Double, Double, Double, Double, String, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Stainless Steel Railing") }
  var lengthStr by remember { mutableStateOf("25") }
  var widthStr by remember { mutableStateOf("3.5") }
  var heightStr by remember { mutableStateOf("0") }
  var qtyStr by remember { mutableStateOf("1") }
  var unit by remember { mutableStateOf("RFT") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalCard,
    title = {
      Text("Log Site Measurement", color = MetallicGold, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Customer Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Customer Phone") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Site Address / Plot #") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Work Category") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = lengthStr,
            onValueChange = { lengthStr = it },
            label = { Text("Length") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = widthStr,
            onValueChange = { widthStr = it },
            label = { Text("Width/Height") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Unit") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Site Notes (Grade, floor, special fittings)") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val l = lengthStr.toDoubleOrNull() ?: 0.0
            val w = widthStr.toDoubleOrNull() ?: 0.0
            val h = heightStr.toDoubleOrNull() ?: 0.0
            val qty = qtyStr.toDoubleOrNull() ?: 1.0
            onConfirm(name, phone, address, category, l, w, h, qty, unit, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
      ) {
        Text("Save Measurement")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SteelSilver)
      }
    }
  )
}
