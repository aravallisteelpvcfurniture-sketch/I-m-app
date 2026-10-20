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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderItem
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PdfInvoiceGenerator
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderManagementScreen(
  viewModel: MainViewModel
) {
  val context = LocalContext.current
  val orders by viewModel.orders.collectAsState()

  var selectedStatusFilter by remember { mutableStateOf("ALL") }
  var orderToUpdate by remember { mutableStateOf<OrderItem?>(null) }

  val statusOptions = listOf(
    "ALL",
    "NEW",
    "MEASUREMENT",
    "ESTIMATE_SENT",
    "APPROVED",
    "WIP",
    "INSTALLATION",
    "COMPLETED",
    "CANCELLED"
  )

  val filteredOrders = orders.filter {
    selectedStatusFilter == "ALL" || it.status == selectedStatusFilter
  }

  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
      .padding(16.dp)
  ) {
    Text(
      text = "Order Workflow & Fabrication Stages",
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MetallicGold
      )
    )
    Text(
      text = "Live job status tracking from Site Measurement to Final Installation",
      style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Status Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(statusOptions) { status ->
        FilterChip(
          selected = selectedStatusFilter == status,
          onClick = { selectedStatusFilter = status },
          label = { Text(status.replace("_", " ")) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MetallicGold,
            selectedLabelColor = CharcoalDark,
            containerColor = CharcoalSurface,
            labelColor = SteelSilver
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (filteredOrders.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.FormatListBulleted,
            contentDescription = null,
            tint = SteelSilver,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No orders found in '$selectedStatusFilter' stage",
            style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver)
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
      ) {
        items(filteredOrders) { order ->
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
                    text = order.orderNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                      color = MetallicGold,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  Text(
                    text = order.customerName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      color = OffWhite,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      when (order.status) {
                        "COMPLETED" -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                        "WIP", "INSTALLATION" -> MetallicGold.copy(alpha = 0.2f)
                        "CANCELLED" -> Color(0xFFC62828).copy(alpha = 0.2f)
                        else -> Color(0xFF1565C0).copy(alpha = 0.2f)
                      }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = order.status.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = when (order.status) {
                        "COMPLETED" -> Color(0xFF81C784)
                        "WIP", "INSTALLATION" -> MetallicGold
                        "CANCELLED" -> Color(0xFFFF8A80)
                        else -> Color(0xFF90CAF9)
                      },
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Site: ${order.siteAddress} • ${order.category}",
                style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
              )

              if (order.assignedStaff.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Engineering, contentDescription = null, tint = MetallicGoldLight, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Lead Fabricator: ${order.assignedStaff}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MetallicGoldLight)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Total: ${inrFormatter.format(order.totalAmount)}",
                  style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Advance: ${inrFormatter.format(order.advancePaid)}",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF81C784))
                )
                Text(
                  text = "Bal: ${inrFormatter.format(order.remainingBalance)}",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF8A65), fontWeight = FontWeight.Bold)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  if (order.customerPhone.isNotBlank()) {
                    IconButton(
                      onClick = { PdfInvoiceGenerator.makePhoneCall(context, order.customerPhone) },
                      modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(CharcoalSurface)
                    ) {
                      Icon(Icons.Default.Call, contentDescription = "Call", tint = MetallicGold, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                      onClick = {
                        PdfInvoiceGenerator.openWhatsAppChat(
                          context,
                          order.customerPhone,
                          "Namaste ${order.customerName}, update regarding Order #${order.orderNumber} (${order.category}): Current status is ${order.status.replace("_", " ")}."
                        )
                      },
                      modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(CharcoalSurface)
                    ) {
                      Icon(Icons.Default.Campaign, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                    }
                  }
                }

                OutlinedButton(
                  onClick = { orderToUpdate = order },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicGold)
                ) {
                  Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Update Stage")
                }
              }
            }
          }
        }
      }
    }
  }

  // Stage Update Dialog
  orderToUpdate?.let { targetOrder ->
    var newStatus by remember { mutableStateOf(targetOrder.status) }
    val editableStatuses = listOf(
      "NEW", "MEASUREMENT", "ESTIMATE_SENT", "APPROVED", "WIP", "INSTALLATION", "COMPLETED", "CANCELLED"
    )

    AlertDialog(
      onDismissRequest = { orderToUpdate = null },
      containerColor = CharcoalCard,
      title = {
        Text("Update Order Status", color = MetallicGold, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Select current fabrication/site stage for Order ${targetOrder.orderNumber}:",
            style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
          )
          editableStatuses.forEach { status ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (newStatus == status) MetallicGold.copy(alpha = 0.2f) else CharcoalSurface)
                .clickable { newStatus = status }
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = status.replace("_", " "),
                color = if (newStatus == status) MetallicGold else OffWhite,
                fontWeight = if (newStatus == status) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateOrderStatus(targetOrder.id, newStatus)
            orderToUpdate = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
        ) {
          Text("Save Stage")
        }
      },
      dismissButton = {
        TextButton(onClick = { orderToUpdate = null }) {
          Text("Cancel", color = SteelSilver)
        }
      }
    )
  }
}
