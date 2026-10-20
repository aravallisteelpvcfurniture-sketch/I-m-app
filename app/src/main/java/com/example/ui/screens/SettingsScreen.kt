package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopInfo
import com.example.data.model.UserRole
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
  authViewModel: AuthViewModel,
  mainViewModel: MainViewModel
) {
  val context = LocalContext.current
  val authState by authViewModel.uiState.collectAsState()
  val selectedRole by authViewModel.selectedRole.collectAsState()
  val shopInfo = remember { ShopInfo() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Shop Settings & Profile",
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MetallicGold
      )
    )
    Text(
      text = "Business identity, GSTIN, Bank details, quotation terms & role access",
      style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
    )

    // User Session Profile Card
    if (authState is AuthUiState.Authenticated) {
      val user = (authState as AuthUiState.Authenticated).user
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(MetallicGold),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.displayName?.take(1) ?: "A",
              style = MaterialTheme.typography.titleLarge.copy(color = CharcoalDark, fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = user.displayName ?: "Aravalli Partner",
              style = MaterialTheme.typography.titleMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
            )
            Text(
              text = user.email ?: "",
              style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
            )
            Text(
              text = "Role: ${selectedRole.name}",
              style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    // Role Switcher for RBAC Demonstration
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Switch Active Permission Role",
          style = MaterialTheme.typography.titleMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Admin has full rights; Staff can log measurements/orders; Customers view estimates & catalogue.",
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          UserRole.values().forEach { role ->
            FilterChip(
              selected = selectedRole == role,
              onClick = { authViewModel.setSelectedRole(role) },
              label = { Text(role.name) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MetallicGold,
                selectedLabelColor = CharcoalDark,
                containerColor = CharcoalCard,
                labelColor = SteelSilver
              )
            )
          }
        }
      }
    }

    // Shop Profile Details Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Storefront, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Business & GST Details",
            style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
          )
        }

        Divider(color = CharcoalBorder, modifier = Modifier.padding(vertical = 4.dp))

        SettingRow("Firm Name", shopInfo.name)
        SettingRow("Proprietor", shopInfo.ownerName)
        SettingRow("GSTIN Number", shopInfo.gstin)
        SettingRow("Address", shopInfo.address)
        SettingRow("Phone / WhatsApp", shopInfo.phone)
        SettingRow("Email", shopInfo.email)
        SettingRow("Currency", "Indian Rupee (INR ₹)")

        Divider(color = CharcoalBorder, modifier = Modifier.padding(vertical = 4.dp))

        Text(
          text = "Bank & UPI Details for Invoices",
          style = MaterialTheme.typography.labelMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
        )
        SettingRow("Bank Name", shopInfo.bankName)
        SettingRow("Account #", shopInfo.accountNumber)
        SettingRow("IFSC Code", shopInfo.ifscCode)
        SettingRow("UPI ID", shopInfo.upiId)
      }
    }

    // Gemini AI Configuration Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Architecture, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Gemini AI Engine (Auto-Build)",
            style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
          )
        }

        Divider(color = CharcoalBorder, modifier = Modifier.padding(vertical = 4.dp))

        SettingRow("Model", "gemini-3.5-flash")
        SettingRow("Build Status", if (com.example.util.GeminiService.isApiKeyConfigured()) "Auto-Injected (Active)" else "Pending Secrets Key")
        SettingRow("API Key", com.example.util.GeminiService.getMaskedApiKey())
        SettingRow("Features", "AI Greetings, Offers & Copywriting")
      }
    }

    // Sample Data Reload
    OutlinedButton(
      onClick = { mainViewModel.seedSampleData() },
      modifier = Modifier.fillMaxWidth().height(48.dp),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicGold),
      border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MetallicGold))
    ) {
      Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Reload Showroom Portfolio Data")
    }

    // Sign Out Button
    Button(
      onClick = { authViewModel.signOut(context) },
      modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_sign_out"),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828), contentColor = Color.White),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Sign Out of Account")
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Composable
private fun SettingRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver))
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontWeight = FontWeight.Medium),
      modifier = Modifier.widthIn(max = 220.dp)
    )
  }
}
