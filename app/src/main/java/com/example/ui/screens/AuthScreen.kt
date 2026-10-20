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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
  viewModel: AuthViewModel,
  onAuthSuccess: () -> Unit
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  val selectedRole by viewModel.selectedRole.collectAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(CharcoalDark, CharcoalSurface, Color(0xFF0D0F12))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      // Brand Logo Crest
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(MetallicGold, Color(0xFF997A15))
            )
          )
          .border(2.dp, MetallicGoldLight, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Architecture,
          contentDescription = "Aravalli Logo",
          tint = CharcoalDark,
          modifier = Modifier.size(52.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "ARAVALLI",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 4.sp,
          color = MetallicGold
        )
      )

      Text(
        text = "STEEL & PVC FURNITURE",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = OffWhite
        )
      )

      Text(
        text = "Stainless Steel Railing • Glass Balcony • Modular PVC Interiors",
        style = MaterialTheme.typography.bodySmall.copy(
          color = SteelSilver,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Role Selection Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MetallicGold.copy(alpha = 0.5f), Color.Transparent)))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Select Login Role",
            style = MaterialTheme.typography.labelMedium.copy(
              color = MetallicGoldLight,
              fontWeight = FontWeight.SemiBold
            )
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            UserRole.values().forEach { role ->
              FilterChip(
                selected = selectedRole == role,
                onClick = { viewModel.setSelectedRole(role) },
                label = { Text(role.name) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MetallicGold,
                  selectedLabelColor = CharcoalDark,
                  containerColor = CharcoalSurface,
                  labelColor = SteelSilver
                ),
                modifier = Modifier.testTag("role_chip_${role.name.lowercase()}")
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Google Sign-In Action
      Button(
        onClick = { viewModel.signInWithGoogle(context) },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("google_sign_in_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MetallicGold,
          contentColor = CharcoalDark
        ),
        shape = RoundedCornerShape(14.dp),
        enabled = uiState !is AuthUiState.Loading
      ) {
        if (uiState is AuthUiState.Loading) {
          CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = CharcoalDark,
            strokeWidth = 2.dp
          )
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Google Secure Auth",
              tint = CharcoalDark,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Sign in with Google",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }

      // Error message if any
      if (uiState is AuthUiState.Error) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = (uiState as AuthUiState.Error).message,
          color = Color(0xFFFF6B6B),
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Features badge grid
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeatureItem("SS 304 / 202 Railing & Toughened Glass Balcony Estimates")
        FeatureItem("Waterproof High-Density PVC Modular Kitchens & Wardrobes")
        FeatureItem("GST Quotations & Tax Invoices with Instant WhatsApp Sharing")
        FeatureItem("Site Measurement Tracker & Customer CRM Ledger")
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Udaipur & Ahmedabad Highway Showroom • Contact: +91 94140 12345",
        style = MaterialTheme.typography.labelSmall.copy(
          color = SteelSilver.copy(alpha = 0.7f),
          textAlign = TextAlign.Center
        )
      )
    }
  }
}

@Composable
private fun FeatureItem(text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = MetallicGold,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall.copy(
        color = OffWhite.copy(alpha = 0.85f)
      )
    )
  }
}
