package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderItem
import com.example.data.model.ShopInfo
import com.example.data.model.UserRole
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelChrome
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PdfInvoiceGenerator
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
  authViewModel: AuthViewModel,
  mainViewModel: MainViewModel,
  onNavigateToEstimates: () -> Unit,
  onNavigateToMeasurements: () -> Unit,
  onNavigateToCalculator: () -> Unit,
  onNavigateToCatalogue: () -> Unit,
  onNavigateToCustomers: () -> Unit,
  onNavigateToOrders: () -> Unit,
  onNavigateToMarketing: () -> Unit
) {
  val context = LocalContext.current
  val selectedRole by authViewModel.selectedRole.collectAsState()
  val orders by mainViewModel.orders.collectAsState()
  val quotations by mainViewModel.quotations.collectAsState()
  val measurements by mainViewModel.measurements.collectAsState()
  val payments by mainViewModel.payments.collectAsState()

  val totalSales = orders.sumOf { it.totalAmount }
  val totalPaid = orders.sumOf { it.advancePaid } + payments.sumOf { it.amount }
  val totalOutstanding = orders.sumOf { it.remainingBalance }
  val activeOrdersCount = orders.count { it.status != "COMPLETED" && it.status != "CANCELLED" }

  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ARAVALLI",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 2.sp,
              color = MetallicGold
            )
          )
          Text(
            text = "Steel & PVC Furniture CRM",
            style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Role badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MetallicGold.copy(alpha = 0.2f))
              .border(1.dp, MetallicGold, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = selectedRole.name,
              style = MaterialTheme.typography.labelSmall.copy(
                color = MetallicGold,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              PdfInvoiceGenerator.makePhoneCall(context, ShopInfo().phone)
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(CharcoalCard)
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "Call Showroom",
              tint = MetallicGold,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = {
              PdfInvoiceGenerator.openWhatsAppChat(
                context,
                ShopInfo().whatsappPhone,
                "Namaste Aravalli Steel! I have an inquiry regarding Railing & PVC Furniture."
              )
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(CharcoalCard)
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = "WhatsApp",
              tint = Color(0xFF25D366),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // Hero KPI Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MetallicGold, CharcoalBorder)))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "FINANCIAL SUMMARY & PIPELINE",
            style = MaterialTheme.typography.labelSmall.copy(
              color = SteelSilver,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            KpiStatItem("Total Projects", inrFormatter.format(totalSales), MetallicGold)
            KpiStatItem("Outstanding", inrFormatter.format(totalOutstanding), Color(0xFFFF7043))
          }
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            KpiStatItem("Active Orders", "$activeOrdersCount in progress", SteelChrome)
            KpiStatItem("Site Visits", "${measurements.size} logged", MetallicGoldLight)
          }
        }
      }
    }

    // Gemini AI Assistant Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .clickable { onNavigateToMarketing() }
          .testTag("gemini_home_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(Color(0xFF2E7D32), MetallicGold))
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFF1B5E20)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MetallicGold,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Gemini AI Assistant",
                color = Color(0xFF81C784),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
              )
              Spacer(modifier = Modifier.width(6.dp))
              Badge(
                containerColor = MetallicGold,
                contentColor = CharcoalDark
              ) {
                Text("Connected", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
              }
            }
            Text(
              text = "AI Posters, WhatsApp Marketing Copy & Instant Estimations",
              color = SteelSilver,
              style = MaterialTheme.typography.bodySmall
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Gemini AI",
            tint = MetallicGold,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Quick Action Grid Header
    item {
      Text(
        text = "Quick Actions",
        style = MaterialTheme.typography.titleMedium.copy(
          color = OffWhite,
          fontWeight = FontWeight.Bold
        )
      )
    }

    // Quick Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "New Estimate",
          icon = Icons.Default.Description,
          subtitle = "Create & PDF",
          modifier = Modifier
            .weight(1f)
            .testTag("action_new_estimate"),
          onClick = onNavigateToEstimates
        )
        QuickActionCard(
          title = "Site Measure",
          icon = Icons.Default.SquareFoot,
          subtitle = "Save Dimensions",
          modifier = Modifier
            .weight(1f)
            .testTag("action_site_measure"),
          onClick = onNavigateToMeasurements
        )
        QuickActionCard(
          title = "Rate Calc",
          icon = Icons.Default.Calculate,
          subtitle = "Steel / PVC / Glass",
          modifier = Modifier
            .weight(1f)
            .testTag("action_rate_calc"),
          onClick = onNavigateToCalculator
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "Catalogue",
          icon = Icons.Default.PhotoLibrary,
          subtitle = "Designs & Specs",
          modifier = Modifier
            .weight(1f)
            .testTag("action_catalogue"),
          onClick = onNavigateToCatalogue
        )
        QuickActionCard(
          title = "Add Customer",
          icon = Icons.Default.PersonAdd,
          subtitle = "CRM Contacts",
          modifier = Modifier
            .weight(1f)
            .testTag("action_add_customer"),
          onClick = onNavigateToCustomers
        )
        QuickActionCard(
          title = "Posters",
          icon = Icons.Default.Campaign,
          subtitle = "Festival Promo",
          modifier = Modifier
            .weight(1f)
            .testTag("action_marketing"),
          onClick = onNavigateToMarketing
        )
      }
    }

    // Products & Services Categories
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Our Core Fabrication Works",
          style = MaterialTheme.typography.titleMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "Browse",
          color = MetallicGold,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.clickable { onNavigateToCatalogue() }
        )
      }
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        val categories = listOf(
          Pair("SS 304 Railings", Icons.Default.Architecture),
          Pair("Aluminium Glass", Icons.Default.Engineering),
          Pair("PVC Furniture", Icons.Default.Weekend),
          Pair("Modular Kitchens", Icons.Default.Kitchen),
          Pair("Wardrobes & Lofts", Icons.Default.Weekend),
          Pair("TV Units & Louvers", Icons.Default.Tv)
        )
        items(categories) { (name, icon) ->
          CategoryPill(name, icon, onClick = onNavigateToCatalogue)
        }
      }
    }

    // Recent Active Orders Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Recent Orders & Work Status",
          style = MaterialTheme.typography.titleMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "View All",
          color = MetallicGold,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.clickable { onNavigateToOrders() }
        )
      }
    }

    if (orders.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No orders created yet",
              style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = onNavigateToEstimates,
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Create First Estimate")
            }
          }
        }
      }
    } else {
      items(orders.take(5)) { order ->
        RecentOrderRow(order = order, onClick = onNavigateToOrders)
      }
    }
  }
}

@Composable
private fun KpiStatItem(label: String, value: String, valueColor: Color) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = valueColor
      )
    )
  }
}

@Composable
private fun QuickActionCard(
  title: String,
  icon: ImageVector,
  subtitle: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .height(104.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CharcoalBorder, Color.Transparent)))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(MetallicGold.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MetallicGold,
          modifier = Modifier.size(20.dp)
        )
      }
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = OffWhite
          )
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(
            color = SteelSilver,
            fontSize = 10.sp
          )
        )
      }
    }
  }
}

@Composable
private fun CategoryPill(name: String, icon: ImageVector, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CharcoalSurface)
      .border(1.dp, CharcoalBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = name,
      tint = MetallicGold,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = name,
      style = MaterialTheme.typography.bodySmall.copy(
        color = OffWhite,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
private fun RecentOrderRow(order: OrderItem, onClick: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, Color.Transparent)))
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
          text = order.customerName,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "${order.orderNumber} • ${order.category}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = SteelSilver
          )
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
              when (order.status) {
                "COMPLETED" -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                "WIP", "INSTALLATION" -> MetallicGold.copy(alpha = 0.2f)
                else -> Color(0xFF1565C0).copy(alpha = 0.2f)
              }
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = order.status.replace("_", " "),
            style = MaterialTheme.typography.labelSmall.copy(
              color = when (order.status) {
                "COMPLETED" -> Color(0xFF81C784)
                "WIP", "INSTALLATION" -> MetallicGold
                else -> Color(0xFF90CAF9)
              },
              fontWeight = FontWeight.Bold
            )
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Bal: ₹${order.remainingBalance.toInt()}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (order.remainingBalance > 0) Color(0xFFFFAB91) else Color(0xFF81C784)
          )
        )
      }
    }
  }
}
