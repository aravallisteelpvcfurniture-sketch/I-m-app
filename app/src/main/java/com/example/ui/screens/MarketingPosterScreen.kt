package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.data.model.ShopInfo
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.util.GeminiService
import com.example.util.PdfInvoiceGenerator
import kotlinx.coroutines.launch

data class PosterTemplate(
  val id: String,
  val title: String,
  val headerText: String,
  val festiveWish: String,
  val offerHighlight: String,
  val accentColor: Color,
  val isAiGenerated: Boolean = false
)

@Composable
fun MarketingPosterScreen() {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val shopInfo = remember { ShopInfo() }

  val defaultTemplates = remember {
    mutableStateListOf(
      PosterTemplate(
        id = "diwali",
        title = "Diwali Festival Greetings",
        headerText = "SHUBH DEEPAVALI",
        festiveWish = "May the divine festival of lights illuminate your home with peace, luxury & prosperity!",
        offerHighlight = "Exclusive Festive Offer: Up to 15% OFF on Jindal SS 304 Railings & Modular PVC Kitchens",
        accentColor = Color(0xFFFFB300)
      ),
      PosterTemplate(
        id = "griha_pravesh",
        title = "Griha Pravesh Mubarak",
        headerText = "GRIHA PRAVESH MUBARAK",
        festiveWish = "Heartiest congratulations on your dream new home! Wishing you blissful moments and lifelong happiness.",
        offerHighlight = "Complete Home Interior Package: SS Glass Balconies, Waterproof PVC Wardrobes & Fluted TV Units",
        accentColor = MetallicGold
      ),
      PosterTemplate(
        id = "factory_offer",
        title = "Factory Direct Pricing",
        headerText = "FACTORY DIRECT PRICING",
        festiveWish = "Premium architectural aesthetics crafted with 100% genuine Jindal SS 304 and termite-proof PVC boards.",
        offerHighlight = "Book Free Site Measurement & 3D Consultation Today! Free Site Visit across Udaipur & NH-8.",
        accentColor = Color(0xFF64B5F6)
      )
    )
  }

  var selectedTemplate by remember { mutableStateOf(defaultTemplates[0]) }
  var recipientName by remember { mutableStateOf("Valued Client") }

  // Gemini AI Generation State
  var showAiSection by remember { mutableStateOf(true) }
  var selectedOccasion by remember { mutableStateOf("Diwali") }
  var customFocusInput by remember { mutableStateOf("SS 304 Railings & Modular PVC Kitchen") }
  var isAiGenerating by remember { mutableStateOf(false) }
  var showSecurityWarningDialog by remember { mutableStateOf(false) }

  val occasionPresets = listOf(
    "Diwali", "Griha Pravesh", "Holi", "Navratri", "New Year", "Eid", "Monsoon Special", "Factory Anniversary"
  )

  val isGeminiReady = remember { GeminiService.isApiKeyConfigured() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Marketing & Festival Posters",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MetallicGold
          )
        )
        Text(
          text = "Generate branded greetings with Gemini AI & share on WhatsApp",
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
        )
      }

      IconButton(onClick = { showSecurityWarningDialog = true }) {
        Icon(
          Icons.Default.Security,
          contentDescription = "Security Notice",
          tint = MetallicGold
        )
      }
    }

    // Gemini AI Assistant Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = Brush.linearGradient(listOf(MetallicGold, CharcoalBorder))
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(20.dp))
            Text(
              text = "Gemini AI Creative Copywriter",
              style = MaterialTheme.typography.titleMedium.copy(
                color = MetallicGold,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isGeminiReady) Color(0xFF1B5E20) else Color(0xFFE65100))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                if (isGeminiReady) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = if (isGeminiReady) "Auto-Built Key Active" else "Key Needed in Secrets",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
              )
            }
          }
        }

        Text(
          text = "Powered by Google Gemini 3.5 Flash: Auto-crafts personalized festive poetry and commercial incentives for your architectural furniture clients.",
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
        )

        // Occasion Chips
        Text(
          text = "Select Occasion / Campaign:",
          style = MaterialTheme.typography.labelMedium.copy(color = OffWhite, fontWeight = FontWeight.SemiBold)
        )
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(occasionPresets) { occ ->
            val isSelected = selectedOccasion == occ
            FilterChip(
              selected = isSelected,
              onClick = { selectedOccasion = occ },
              label = { Text(occ) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MetallicGold,
                selectedLabelColor = CharcoalDark,
                containerColor = CharcoalSurface,
                labelColor = OffWhite
              )
            )
          }
        }

        // Custom Focus / Products
        OutlinedTextField(
          value = customFocusInput,
          onValueChange = { customFocusInput = it },
          label = { Text("Product Highlights / Offer Details") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MetallicGold,
            unfocusedBorderColor = CharcoalBorder,
            focusedTextColor = OffWhite,
            unfocusedTextColor = OffWhite
          )
        )

        // Generate Button
        Button(
          onClick = {
            if (!isGeminiReady) {
              Toast.makeText(context, "Gemini API key is missing. Add GEMINI_API_KEY in Secrets panel.", Toast.LENGTH_LONG).show()
              return@Button
            }
            isAiGenerating = true
            coroutineScope.launch {
              val result = GeminiService.generateFestivalOrPromoPoster(
                occasion = selectedOccasion,
                recipientName = recipientName.ifBlank { "Valued Client" },
                shopName = shopInfo.name,
                customFocus = customFocusInput
              )
              isAiGenerating = false
              result.onSuccess { aiContent ->
                val newTmpl = PosterTemplate(
                  id = "ai_${System.currentTimeMillis()}",
                  title = "AI: $selectedOccasion",
                  headerText = aiContent.headerText,
                  festiveWish = aiContent.festiveWish,
                  offerHighlight = aiContent.offerHighlight,
                  accentColor = when {
                    selectedOccasion.contains("Diwali", ignoreCase = true) -> Color(0xFFFFB300)
                    selectedOccasion.contains("Holi", ignoreCase = true) -> Color(0xFFE91E63)
                    selectedOccasion.contains("Eid", ignoreCase = true) -> Color(0xFF4CAF50)
                    selectedOccasion.contains("Griha", ignoreCase = true) -> MetallicGold
                    else -> Color(0xFF00E5FF)
                  },
                  isAiGenerated = true
                )
                defaultTemplates.add(0, newTmpl)
                selectedTemplate = newTmpl
                Toast.makeText(context, "Gemini 3.5 Flash generated greeting!", Toast.LENGTH_SHORT).show()
              }.onFailure { err ->
                Toast.makeText(context, "AI Error: ${err.localizedMessage ?: "Failed to generate"}", Toast.LENGTH_LONG).show()
              }
            }
          },
          enabled = !isAiGenerating,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_gemini_generate"),
          colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark),
          shape = RoundedCornerShape(12.dp)
        ) {
          if (isAiGenerating) {
            CircularProgressIndicator(color = CharcoalDark, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gemini 3.5 Flash writing copy...", fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate with Gemini AI", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Template Selector
    Text(
      text = "Available Poster Templates:",
      style = MaterialTheme.typography.titleSmall.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      defaultTemplates.take(6).forEach { tmpl ->
        val isSelected = selectedTemplate.id == tmpl.id
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MetallicGold.copy(alpha = 0.15f) else CharcoalSurface)
            .border(1.dp, if (isSelected) MetallicGold else CharcoalBorder, RoundedCornerShape(12.dp))
            .clickable { selectedTemplate = tmpl }
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (tmpl.isAiGenerated) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(MetallicGold)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("AI", color = CharcoalDark, fontSize = 9.sp, fontWeight = FontWeight.Black)
              }
            }
            Text(
              text = tmpl.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isSelected) MetallicGold else OffWhite,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            )
          }
          if (isSelected) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(18.dp))
          }
        }
      }
    }

    // Recipient Name
    OutlinedTextField(
      value = recipientName,
      onValueChange = { recipientName = it },
      label = { Text("Client / Family Name to Greet") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MetallicGold,
        unfocusedBorderColor = CharcoalBorder,
        focusedTextColor = OffWhite,
        unfocusedTextColor = OffWhite
      )
    )

    // Poster Preview Card (The actual luxury graphic poster)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = CharcoalCard),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = Brush.linearGradient(listOf(selectedTemplate.accentColor, CharcoalBorder))
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Logo Crest
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(selectedTemplate.accentColor)
            .border(1.dp, MetallicGoldLight, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Architecture, contentDescription = null, tint = CharcoalDark, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = shopInfo.name.uppercase(),
          style = MaterialTheme.typography.titleMedium.copy(
            color = MetallicGold,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Headline
        Text(
          text = selectedTemplate.headerText,
          style = MaterialTheme.typography.headlineSmall.copy(
            color = selectedTemplate.accentColor,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Dear $recipientName,",
          style = MaterialTheme.typography.titleSmall.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = selectedTemplate.festiveWish,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = OffWhite.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Highlight Offer Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(selectedTemplate.accentColor.copy(alpha = 0.15f))
            .border(1.dp, selectedTemplate.accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Text(
            text = selectedTemplate.offerHighlight,
            style = MaterialTheme.typography.bodySmall.copy(
              color = selectedTemplate.accentColor,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }

        if (selectedTemplate.isAiGenerated) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Personalized with Gemini 3.5 Flash",
              style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, fontSize = 10.sp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "${shopInfo.address}\nCall / WhatsApp: ${shopInfo.phone}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = SteelSilver,
            textAlign = TextAlign.Center
          )
        )
      }
    }

    // Share Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val shareText = "✨ ${selectedTemplate.headerText} ✨\n\nDear $recipientName,\n${selectedTemplate.festiveWish}\n\n🌟 ${selectedTemplate.offerHighlight}\n\nWarm regards,\n*${shopInfo.name}*\n${shopInfo.tagline}\n📍 ${shopInfo.address}\n📞 Call: ${shopInfo.phone}"
          val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "${selectedTemplate.headerText} from ${shopInfo.name}")
          }
          context.startActivity(Intent.createChooser(intent, "Share Greeting via"))
        },
        modifier = Modifier.weight(1f).height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share Sheet")
      }

      Button(
        onClick = {
          val shareText = "✨ *${selectedTemplate.headerText}* ✨\n\nDear $recipientName,\n${selectedTemplate.festiveWish}\n\n🌟 ${selectedTemplate.offerHighlight}\n\nWarm regards,\n*${shopInfo.name}*\n📞 ${shopInfo.phone}"
          PdfInvoiceGenerator.openWhatsAppChat(context, "", shareText)
        },
        modifier = Modifier.weight(1f).height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("WhatsApp")
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }

  // Security Warning Dialog as Mandated by Android Secret Management Skill
  if (showSecurityWarningDialog) {
    AlertDialog(
      onDismissRequest = { showSecurityWarningDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = MetallicGold)
          Text("Security Notice", color = MetallicGold, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
            color = OffWhite,
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Key Status: ${if (isGeminiReady) "Configured in BuildConfig (${GeminiService.getMaskedApiKey()})" else "Not configured"}",
            color = SteelSilver,
            style = MaterialTheme.typography.bodySmall
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showSecurityWarningDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
        ) {
          Text("Understood")
        }
      },
      containerColor = CharcoalCard
    )
  }
}
