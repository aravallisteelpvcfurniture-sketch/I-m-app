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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.Customer
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
fun CustomerCrmScreen(
  viewModel: MainViewModel
) {
  val context = LocalContext.current
  val customers by viewModel.customers.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  val filteredCustomers = customers.filter { cust ->
    cust.name.contains(searchQuery, ignoreCase = true) ||
      cust.phone.contains(searchQuery, ignoreCase = true) ||
      cust.siteAddress.contains(searchQuery, ignoreCase = true)
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
        text = "Customer CRM & Sites",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Clients, site locations, direct phone dialer and WhatsApp contact",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Search
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by customer name, phone, or site location...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MetallicGold) },
        modifier = Modifier.fillMaxWidth().testTag("crm_search_bar"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CharcoalSurface,
          unfocusedContainerColor = CharcoalSurface,
          focusedBorderColor = MetallicGold,
          unfocusedBorderColor = CharcoalBorder,
          focusedTextColor = OffWhite,
          unfocusedTextColor = OffWhite,
          focusedPlaceholderColor = SteelSilver,
          unfocusedPlaceholderColor = SteelSilver
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      if (filteredCustomers.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.People, contentDescription = null, tint = SteelSilver, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No customer contacts found", style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
            ) {
              Text("Add First Customer")
            }
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(filteredCustomers) { cust ->
            CustomerCard(
              customer = cust,
              onCall = { PdfInvoiceGenerator.makePhoneCall(context, cust.phone) },
              onWhatsApp = {
                PdfInvoiceGenerator.openWhatsAppChat(
                  context,
                  cust.phone,
                  "Namaste ${cust.name}! Touching base from Aravalli Steel & PVC Furniture regarding your site project."
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
        .testTag("fab_add_customer")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Customer")
    }
  }

  if (showAddDialog) {
    AddCustomerDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, phone, address, city, email, notes ->
        viewModel.addCustomer(name, phone, address, city, email, notes) {
          showAddDialog = false
        }
      }
    )
  }
}

@Composable
private fun CustomerCard(
  customer: Customer,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, Color.Transparent)))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = customer.name,
          style = MaterialTheme.typography.titleMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = customer.phone,
          style = MaterialTheme.typography.bodyMedium.copy(color = MetallicGold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = SteelSilver, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${customer.siteAddress}, ${customer.city}",
            style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
          )
        }
        if (customer.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Notes: ${customer.notes}",
            style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver.copy(alpha = 0.7f))
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (customer.phone.isNotBlank()) {
          IconButton(
            onClick = onCall,
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(CharcoalSurface)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = MetallicGold, modifier = Modifier.size(20.dp))
          }
          IconButton(
            onClick = onWhatsApp,
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(CharcoalSurface)
          ) {
            Icon(Icons.Default.Campaign, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(20.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun AddCustomerDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, String, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("Udaipur") }
  var email by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalCard,
    title = {
      Text("New Customer Profile", color = MetallicGold, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
          label = { Text("Mobile Number *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Site Address *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = city,
          onValueChange = { city = it },
          label = { Text("City") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Project Notes") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
            onConfirm(name, phone, address, city, email, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
      ) {
        Text("Save Customer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SteelSilver)
      }
    }
  )
}
