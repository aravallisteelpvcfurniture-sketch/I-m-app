package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.CharcoalBorder
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
  onDemoMode: () -> Unit = {},
  onAuthSuccess: () -> Unit = {}
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  val selectedRole by viewModel.selectedRole.collectAsState()
  var showFirebaseGuide by remember { mutableStateOf(false) }

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
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // Brand Logo Crest
      Box(
        modifier = Modifier
          .size(80.dp)
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
          modifier = Modifier.size(48.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Role Selection Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MetallicGold.copy(alpha = 0.5f), Color.Transparent)))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
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

      Spacer(modifier = Modifier.height(18.dp))

      // Primary Google Sign-In Button
      Button(
        onClick = { viewModel.signInWithGoogle(context) },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
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

      Spacer(modifier = Modifier.height(12.dp))

      // Offline Demo Mode Bypass Button
      OutlinedButton(
        onClick = onDemoMode,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("offline_demo_button"),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MetallicGoldLight
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MetallicGoldLight, MetallicGold))),
        shape = RoundedCornerShape(14.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Demo Access",
            tint = MetallicGoldLight,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ऑफलाइन पार्टनर मोड (Explore Demo App)",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      // Error Alert Box
      if (uiState is AuthUiState.Error) {
        val error = uiState as AuthUiState.Error
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1515)),
          shape = RoundedCornerShape(12.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFFF5252), Color(0xFF882020))))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = error.message,
              color = Color(0xFFFF8A8A),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium
            )
            if (error.isConfigError || !showFirebaseGuide) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "👉 Firebase SHA-1 सेटअप गाइड देखने के लिए नीचे बटन दबाएं",
                color = MetallicGoldLight,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.clickable { showFirebaseGuide = true }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Expandable Firebase SHA-1 & Setup Helper Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, MetallicGold.copy(alpha = 0.4f))))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showFirebaseGuide = !showFirebaseGuide },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = MetallicGold,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Firebase Google Sign-In सेटअप व SHA-1",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = MetallicGoldLight,
                  fontWeight = FontWeight.Bold
                )
              )
            }
            Icon(
              imageVector = Icons.Default.HelpOutline,
              contentDescription = "Help",
              tint = SteelSilver,
              modifier = Modifier.size(18.dp)
            )
          }

          if (showFirebaseGuide || (uiState is AuthUiState.Error && (uiState as AuthUiState.Error).isConfigError)) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CharcoalBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "गूगल लॉगिन चालू करने के लिए 3 आसान स्टेप्स:",
              style = MaterialTheme.typography.labelMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "1. Firebase Console (console.firebase.google.com) में प्रोजेक्ट aravallisteel-60c94 खोलें।\n" +
                     "2. Authentication -> Sign-in method में जाकर 'Google' को Enable करें।\n" +
                     "3. Project Settings (⚙️) -> Your apps में जाकर नीचे दिया गया SHA-1 Fingerprint Add करें:",
              style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SHA-1 Box with 1-tap copy
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CharcoalDark,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .padding(10.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "SHA-1 FINGERPRINT:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = AuthViewModel.APP_SHA1,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp
                    )
                  )
                }
                OutlinedButton(
                  onClick = {
                    copyToClipboard(context, "SHA-1 Fingerprint", AuthViewModel.APP_SHA1)
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.height(34.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = MetallicGold)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Copy", fontSize = 11.sp, color = MetallicGold)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Package Name Box
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CharcoalDark,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .padding(10.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "PACKAGE NAME:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = AuthViewModel.APP_PACKAGE_NAME,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp
                    )
                  )
                }
                OutlinedButton(
                  onClick = {
                    copyToClipboard(context, "Package Name", AuthViewModel.APP_PACKAGE_NAME)
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.height(34.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = MetallicGold)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Copy", fontSize = 11.sp, color = MetallicGold)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

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

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Udaipur & Ahmedabad Highway Showroom • Contact: +91 94140 12345",
        style = MaterialTheme.typography.labelSmall.copy(
          color = SteelSilver.copy(alpha = 0.7f),
          textAlign = TextAlign.Center
        )
      )
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(label, text)
  clipboard.setPrimaryClip(clip)
  Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
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
