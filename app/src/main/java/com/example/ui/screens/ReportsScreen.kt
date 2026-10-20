package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReportsScreen(
  viewModel: MainViewModel
) {
  val orders by viewModel.orders.collectAsState()
  val quotations by viewModel.quotations.collectAsState()
  val payments by viewModel.payments.collectAsState()

  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  val totalSales = orders.sumOf { it.totalAmount }
  val totalCollected = orders.sumOf { it.advancePaid } + payments.sumOf { it.amount }
  val totalReceivable = orders.sumOf { it.remainingBalance }
  val completedOrders = orders.count { it.status == "COMPLETED" }
  val totalOrdersCount = orders.size

  val completionRate = if (totalOrdersCount > 0) (completedOrders.toFloat() / totalOrdersCount) else 0f

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Reports & Analytics",
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MetallicGold
      )
    )
    Text(
      text = "Revenue summaries, order fulfillment and collection analytics",
      style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
    )

    // Summary Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
      border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MetallicGold, CharcoalBorder)))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "FINANCIAL PERFORMANCE",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MetallicGoldLight,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MetricBlock("Total Order Bookings", inrFormatter.format(totalSales), OffWhite)
          MetricBlock("Total Collections", inrFormatter.format(totalCollected), Color(0xFF81C784))
        }

        Divider(color = CharcoalBorder)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MetricBlock("Pending Dues", inrFormatter.format(totalReceivable), Color(0xFFFF8A65))
          MetricBlock("Estimates Issued", "${quotations.size} Quotes", MetallicGold)
        }
      }
    }

    // Job Completion Progress
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Job Completion Rate", style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold))
          Text("${(completionRate * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold))
        }
        LinearProgressIndicator(
          progress = { completionRate },
          modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
          color = MetallicGold,
          trackColor = CharcoalSurface
        )
        Text(
          text = "$completedOrders completed out of $totalOrdersCount total scheduled installations",
          style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver)
        )
      }
    }

    // Category Distribution
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Category Revenue Distribution",
          style = MaterialTheme.typography.titleMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
        )

        val categories = listOf(
          Pair("Stainless Steel Railing", 0.45f),
          Pair("Aluminium Glass Balcony", 0.25f),
          Pair("PVC Furniture & Wardrobes", 0.18f),
          Pair("Modular Kitchens & Interiors", 0.12f)
        )

        categories.forEach { (name, share) ->
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(name, style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver))
              Text("${(share * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(color = MetallicGold))
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { share },
              modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
              color = MetallicGoldLight,
              trackColor = CharcoalSurface
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MetricBlock(label: String, value: String, valueColor: Color) {
  Column {
    Text(label, style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
    Text(value, style = MaterialTheme.typography.titleMedium.copy(color = valueColor, fontWeight = FontWeight.Bold))
  }
}
