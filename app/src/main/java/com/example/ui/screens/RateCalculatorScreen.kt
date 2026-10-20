package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelChrome
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.MainViewModel
import com.example.util.RateCalculationInput
import com.example.util.RateCalculator
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RateCalculatorScreen(
  viewModel: MainViewModel,
  onCreateEstimateClicked: (category: String, subtotal: Double, total: Double, advance: Double) -> Unit
) {
  val currentInput by viewModel.calculatorInput.collectAsState()
  val result by viewModel.calculatorResult.collectAsState()

  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  var lengthStr by remember(currentInput) { mutableStateOf(currentInput.quantityLength.toString()) }
  var materialRateStr by remember(currentInput) { mutableStateOf(currentInput.materialRatePerUnit.toString()) }
  var glassRateStr by remember(currentInput) { mutableStateOf(currentInput.glassRatePerUnit.toString()) }
  var accessoriesStr by remember(currentInput) { mutableStateOf(currentInput.accessoriesCost.toString()) }
  var labourStr by remember(currentInput) { mutableStateOf(currentInput.labourRatePerUnit.toString()) }
  var transportStr by remember(currentInput) { mutableStateOf(currentInput.transportCost.toString()) }
  var discountStr by remember(currentInput) { mutableStateOf(currentInput.discountPercent.toString()) }
  var taxStr by remember(currentInput) { mutableStateOf(currentInput.taxPercent.toString()) }
  var advanceStr by remember(currentInput) { mutableStateOf(currentInput.advancePercent.toString()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Column {
      Text(
        text = "Accurate Rate Calculator",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Stainless Steel Railing, Aluminium/Glass Balcony & PVC Furniture Costing",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )
    }

    // Presets Carousel
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
      border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, Color.Transparent)))
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "Quick Load Preset Templates",
          style = MaterialTheme.typography.labelMedium.copy(
            color = MetallicGoldLight,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          RateCalculator.presetTemplates.forEach { template ->
            val isSelected = currentInput.subTypeOrGrade == template.subTypeOrGrade
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) MetallicGold.copy(alpha = 0.15f) else CharcoalCard)
                .border(
                  1.dp,
                  if (isSelected) MetallicGold else CharcoalBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable {
                  viewModel.updateCalculatorInput(template)
                }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = template.subTypeOrGrade,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) MetallicGold else OffWhite,
                    fontWeight = FontWeight.SemiBold
                  )
                )
                Text(
                  text = "${template.category} • Base ₹${template.materialRatePerUnit}/${template.unit}",
                  style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver)
                )
              }
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MetallicGold,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }

    // Configurable Input Fields
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
          text = "Dimension & Rate Configuration",
          style = MaterialTheme.typography.titleMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = lengthStr,
            onValueChange = {
              lengthStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(quantityLength = v))
            },
            label = { Text("Length / Area (${currentInput.unit})") },
            modifier = Modifier.weight(1f).testTag("calc_input_length"),
            singleLine = true
          )
          OutlinedTextField(
            value = materialRateStr,
            onValueChange = {
              materialRateStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(materialRatePerUnit = v))
            },
            label = { Text("Material Rate (₹/${currentInput.unit})") },
            modifier = Modifier.weight(1f).testTag("calc_input_material_rate"),
            singleLine = true
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = glassRateStr,
            onValueChange = {
              glassRateStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(glassRatePerUnit = v))
            },
            label = { Text("Glass Infill Rate (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = accessoriesStr,
            onValueChange = {
              accessoriesStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(accessoriesCost = v))
            },
            label = { Text("Hardware/Fittings (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = labourStr,
            onValueChange = {
              labourStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(labourRatePerUnit = v))
            },
            label = { Text("Labour (₹/${currentInput.unit})") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = transportStr,
            onValueChange = {
              transportStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(transportCost = v))
            },
            label = { Text("Local Transport (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = discountStr,
            onValueChange = {
              discountStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(discountPercent = v))
            },
            label = { Text("Discount (%)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = taxStr,
            onValueChange = {
              taxStr = it
              val v = it.toDoubleOrNull() ?: 0.0
              viewModel.updateCalculatorInput(currentInput.copy(taxPercent = v))
            },
            label = { Text("GST Tax (%)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
      }
    }

    // Live Result Breakdown Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
      border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(MetallicGold, CharcoalBorder)))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "COSTING BREAKDOWN",
            style = MaterialTheme.typography.labelMedium.copy(
              color = MetallicGoldLight,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "${currentInput.quantityLength} ${currentInput.unit}",
            style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        CostRow("Raw Material Fabric Cost", inrFormatter.format(result.materialCost))
        if (result.glassCost > 0) {
          CostRow("Toughened/Laminated Glass", inrFormatter.format(result.glassCost))
        }
        CostRow("Accessories, Brackets & Clamps", inrFormatter.format(result.accessoriesCost))
        CostRow("Skilled Labour & Fabrication", inrFormatter.format(result.labourCost))
        CostRow("Site Transport & Delivery", inrFormatter.format(result.transportCost))

        Divider(color = CharcoalBorder, modifier = Modifier.padding(vertical = 4.dp))

        CostRow("Subtotal", inrFormatter.format(result.subtotal), isBold = true)
        if (result.discountAmount > 0) {
          CostRow("Discount (${currentInput.discountPercent}%)", "- ${inrFormatter.format(result.discountAmount)}", color = Color(0xFF81C784))
        }
        CostRow("GST Tax (${currentInput.taxPercent}%)", inrFormatter.format(result.taxAmount))

        // Final Grand Total Highlight
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MetallicGold)
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "FINAL ESTIMATE",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = CharcoalDark
              )
            )
            Text(
              text = inrFormatter.format(result.finalTotal),
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = CharcoalDark
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Advance (${currentInput.advancePercent}%): ${inrFormatter.format(result.advanceAmount)}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Balance: ${inrFormatter.format(result.balanceAmount)}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF8A65), fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    // Assumptions Note
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Fabrication Assumptions & Standards",
            style = MaterialTheme.typography.labelMedium.copy(
              color = MetallicGoldLight,
              fontWeight = FontWeight.Bold
            )
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = result.assumptionsText,
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver),
          lineHeight = 18.sp
        )
      }
    }

    // Action button to convert to Estimate
    Button(
      onClick = {
        onCreateEstimateClicked(
          currentInput.category,
          result.subtotal,
          result.finalTotal,
          result.advanceAmount
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("btn_create_estimate_from_calc"),
      colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.ReceiptLong, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Generate Official Quotation",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Icon(Icons.Default.ArrowForward, contentDescription = null)
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Composable
private fun CostRow(
  label: String,
  amount: String,
  isBold: Boolean = false,
  color: Color = OffWhite
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        color = if (isBold) OffWhite else SteelSilver,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
      )
    )
    Text(
      text = amount,
      style = MaterialTheme.typography.bodySmall.copy(
        color = color,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
      )
    )
  }
}
