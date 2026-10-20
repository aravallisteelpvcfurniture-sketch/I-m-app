package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Quotation
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
fun QuotationScreen(
  viewModel: MainViewModel,
  prefillCategory: String = "",
  prefillSubtotal: Double = 0.0,
  prefillTotal: Double = 0.0,
  prefillAdvance: Double = 0.0
) {
  val context = LocalContext.current
  val quotations by viewModel.quotations.collectAsState()

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var showCreateDialog by remember {
    mutableStateOf(prefillSubtotal > 0.0)
  }

  val tabs = listOf("All Estimates", "Tax Invoices")
  val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  val filteredQuotations = quotations.filter { quote ->
    when (selectedTabIndex) {
      0 -> !quote.isInvoice
      else -> quote.isInvoice
    }
  }

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
        text = "Quotations & Invoicing",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Itemized GST Quotations, Invoices, Terms & PDF WhatsApp Delivery",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Spacer(modifier = Modifier.height(14.dp))

      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = CharcoalSurface,
        contentColor = MetallicGold,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = MetallicGold
          )
        }
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTabIndex == index) MetallicGold else SteelSilver
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (filteredQuotations.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = SteelSilver,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (selectedTabIndex == 0) "No estimates created yet" else "No tax invoices generated yet",
              style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showCreateDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
            ) {
              Text("Create New Quotation")
            }
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(filteredQuotations) { quote ->
            QuotationCard(
              quotation = quote,
              inrFormatter = inrFormatter,
              onSharePdf = {
                PdfInvoiceGenerator.generateAndSharePdf(
                  context = context,
                  quotation = quote,
                  items = emptyList(),
                  shareViaWhatsApp = false
                )
              },
              onWhatsAppPdf = {
                PdfInvoiceGenerator.generateAndSharePdf(
                  context = context,
                  quotation = quote,
                  items = emptyList(),
                  shareViaWhatsApp = true
                )
              },
              onConvertToInvoice = {
                viewModel.convertQuotationToInvoice(quote, context)
              },
              onCall = {
                PdfInvoiceGenerator.makePhoneCall(context, quote.customerPhone)
              }
            )
          }
        }
      }
    }

    FloatingActionButton(
      onClick = { showCreateDialog = true },
      containerColor = MetallicGold,
      contentColor = CharcoalDark,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("fab_create_quotation")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create Quotation")
    }
  }

  if (showCreateDialog) {
    CreateQuotationDialog(
      initialCategory = prefillCategory.ifBlank { "Stainless Steel Railing" },
      initialSubtotal = if (prefillSubtotal > 0) prefillSubtotal else 25000.0,
      initialAdvance = if (prefillAdvance > 0) prefillAdvance else 12500.0,
      onDismiss = { showCreateDialog = false },
      onConfirm = { name, phone, address, category, subtotal, discount, tax, advance, terms, isInvoice ->
        viewModel.createQuotation(
          customerName = name,
          customerPhone = phone,
          siteAddress = address,
          category = category,
          subtotal = subtotal,
          discountAmount = discount,
          taxPercent = tax,
          advanceAmount = advance,
          terms = terms,
          isInvoice = isInvoice,
          onSuccess = { createdQuote ->
            showCreateDialog = false
            // Offer instant PDF generation
            PdfInvoiceGenerator.generateAndSharePdf(
              context = context,
              quotation = createdQuote,
              items = emptyList(),
              shareViaWhatsApp = false
            )
          }
        )
      }
    )
  }
}

@Composable
private fun QuotationCard(
  quotation: Quotation,
  inrFormatter: NumberFormat,
  onSharePdf: () -> Unit,
  onWhatsAppPdf: () -> Unit,
  onConvertToInvoice: () -> Unit,
  onCall: () -> Unit
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
            text = quotation.quotationNumber,
            style = MaterialTheme.typography.titleMedium.copy(
              color = MetallicGold,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = quotation.customerName,
            style = MaterialTheme.typography.bodyLarge.copy(
              color = OffWhite,
              fontWeight = FontWeight.Bold
            )
          )
          if (quotation.customerPhone.isNotBlank()) {
            Text(
              text = "Phone: ${quotation.customerPhone}",
              style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (quotation.isInvoice) Color(0xFF2E7D32).copy(alpha = 0.2f)
              else MetallicGold.copy(alpha = 0.2f)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (quotation.isInvoice) "TAX INVOICE" else quotation.status,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (quotation.isInvoice) Color(0xFF81C784) else MetallicGold,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Site: ${quotation.siteAddress} • ${quotation.category}",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Divider(color = CharcoalBorder, modifier = Modifier.padding(vertical = 10.dp))

      // Financials Summary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Subtotal", style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
          Text(inrFormatter.format(quotation.subtotal), style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite))
        }
        Column {
          Text("GST (${quotation.taxPercent}%)", style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver))
          Text(inrFormatter.format(quotation.taxAmount), style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite))
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Grand Total", style = MaterialTheme.typography.labelSmall.copy(color = MetallicGoldLight))
          Text(inrFormatter.format(quotation.totalAmount), style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Advance: ${inrFormatter.format(quotation.advanceAmount)}",
          style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF81C784), fontWeight = FontWeight.SemiBold)
        )
        Text(
          text = "Balance: ${inrFormatter.format(quotation.balanceAmount)}",
          style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF8A65), fontWeight = FontWeight.SemiBold)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onSharePdf,
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(CharcoalSurface)
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = MetallicGold, modifier = Modifier.size(18.dp))
          }
          IconButton(
            onClick = onWhatsAppPdf,
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(CharcoalSurface)
          ) {
            Icon(Icons.Default.Campaign, contentDescription = "WhatsApp PDF", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
          }
          if (quotation.customerPhone.isNotBlank()) {
            IconButton(
              onClick = onCall,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
            ) {
              Icon(Icons.Default.Call, contentDescription = "Call", tint = SteelSilver, modifier = Modifier.size(18.dp))
            }
          }
        }

        if (!quotation.isInvoice) {
          OutlinedButton(
            onClick = onConvertToInvoice,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicGold),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MetallicGold, MetallicGoldLight)))
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Tax Invoice", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }
  }
}

@Composable
private fun CreateQuotationDialog(
  initialCategory: String,
  initialSubtotal: Double,
  initialAdvance: Double,
  onDismiss: () -> Unit,
  onConfirm: (
    customerName: String,
    customerPhone: String,
    siteAddress: String,
    category: String,
    subtotal: Double,
    discount: Double,
    taxPercent: Double,
    advance: Double,
    terms: String,
    isInvoice: Boolean
  ) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(initialCategory) }
  var subtotalStr by remember { mutableStateOf(initialSubtotal.toInt().toString()) }
  var discountStr by remember { mutableStateOf("0") }
  var taxStr by remember { mutableStateOf("18") }
  var advanceStr by remember { mutableStateOf(initialAdvance.toInt().toString()) }
  var terms by remember {
    mutableStateOf(
      "1. 50% advance with confirmed order.\n2. 40% on material delivery at site.\n3. 10% on completion.\n4. SS 304 Jindal guarantee against rust."
    )
  }
  var isInvoice by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalCard,
    title = {
      Text(
        if (isInvoice) "Create Tax Invoice" else "Create Quotation / Estimate",
        color = MetallicGold,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Customer / Firm Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Customer Mobile (for WhatsApp PDF)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Site Address / Location") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Category / Work Scope") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = subtotalStr,
            onValueChange = { subtotalStr = it },
            label = { Text("Subtotal (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = advanceStr,
            onValueChange = { advanceStr = it },
            label = { Text("Advance (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = discountStr,
            onValueChange = { discountStr = it },
            label = { Text("Discount (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = taxStr,
            onValueChange = { taxStr = it },
            label = { Text("GST (%)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(
            checked = isInvoice,
            onCheckedChange = { isInvoice = it },
            colors = CheckboxDefaults.colors(checkedColor = MetallicGold, checkmarkColor = CharcoalDark)
          )
          Text("Generate directly as Tax Invoice", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val sub = subtotalStr.toDoubleOrNull() ?: 0.0
            val disc = discountStr.toDoubleOrNull() ?: 0.0
            val tax = taxStr.toDoubleOrNull() ?: 18.0
            val adv = advanceStr.toDoubleOrNull() ?: 0.0
            onConfirm(name, phone, address, category, sub, disc, tax, adv, terms, isInvoice)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
      ) {
        Text("Generate PDF & Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SteelSilver)
      }
    }
  )
}
