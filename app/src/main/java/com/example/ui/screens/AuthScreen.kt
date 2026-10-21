package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

  // 0: Login, 1: Sign Up
  var selectedTab by remember { mutableIntStateOf(0) }

  // Form input states
  var emailOrPhone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var fullName by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var validationError by remember { mutableStateOf<String?>(null) }
  var showShaGuide by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(CharcoalDark, CharcoalSurface, Color(0xFF0C0E12))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(28.dp))

      // 1. Brand Logo Crest
      Box(
        modifier = Modifier
          .size(86.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(MetallicGold, Color(0xFF997A15))
            )
          )
          .border(2.dp, MetallicGoldLight, CircleShape)
          .testTag("logo_crest"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Architecture,
          contentDescription = "Aravalli Logo",
          tint = CharcoalDark,
          modifier = Modifier.size(50.dp)
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
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = SteelSilver
        )
      )

      Spacer(modifier = Modifier.height(26.dp))

      // 2. Main Auth Card with Login & Sign Up Tabs
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_card"),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(MetallicGold.copy(alpha = 0.4f), CharcoalBorder)
          )
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Tabs: Login / Sign Up
          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MetallicGold,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = MetallicGold,
                height = 3.dp
              )
            },
            divider = {
              HorizontalDivider(color = CharcoalBorder)
            }
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = {
                selectedTab = 0
                validationError = null
              },
              text = {
                Text(
                  text = "Login",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 0) MetallicGold else SteelSilver
                  )
                )
              },
              modifier = Modifier.testTag("auth_tab_login")
            )
            Tab(
              selected = selectedTab == 1,
              onClick = {
                selectedTab = 1
                validationError = null
              },
              text = {
                Text(
                  text = "Sign Up",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 1) MetallicGold else SteelSilver
                  )
                )
              },
              modifier = Modifier.testTag("auth_tab_signup")
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          if (selectedTab == 0) {
            // LOGIN FORM
            OutlinedTextField(
              value = emailOrPhone,
              onValueChange = {
                emailOrPhone = it
                validationError = null
              },
              label = { Text("Email or Phone") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = CharcoalBorder,
                focusedLabelColor = MetallicGoldLight,
                unfocusedLabelColor = SteelSilver,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = MetallicGold
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = password,
              onValueChange = {
                password = it
                validationError = null
              },
              label = { Text("Password") },
              singleLine = true,
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = SteelSilver
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = CharcoalBorder,
                focusedLabelColor = MetallicGoldLight,
                unfocusedLabelColor = SteelSilver,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = MetallicGold
              )
            )

            if (validationError != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = validationError ?: "",
                color = Color(0xFFFF8A8A),
                style = MaterialTheme.typography.bodySmall
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct Login Action
            Button(
              onClick = {
                if (emailOrPhone.trim().isEmpty()) {
                  validationError = "कृपया अपना Email या Phone नंबर दर्ज करें"
                } else if (password.trim().isEmpty()) {
                  validationError = "कृपया Password दर्ज करें"
                } else {
                  Toast.makeText(context, "लॉगिन सफल! Welcome back.", Toast.LENGTH_SHORT).show()
                  onAuthSuccess()
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MetallicGold,
                contentColor = CharcoalDark
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "Login",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold
                )
              )
            }
          } else {
            // SIGN UP FORM
            OutlinedTextField(
              value = fullName,
              onValueChange = {
                fullName = it
                validationError = null
              },
              label = { Text("Full Name") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_name_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = CharcoalBorder,
                focusedLabelColor = MetallicGoldLight,
                unfocusedLabelColor = SteelSilver,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = MetallicGold
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = emailOrPhone,
              onValueChange = {
                emailOrPhone = it
                validationError = null
              },
              label = { Text("Email or Phone") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_email_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = CharcoalBorder,
                focusedLabelColor = MetallicGoldLight,
                unfocusedLabelColor = SteelSilver,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = MetallicGold
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = password,
              onValueChange = {
                password = it
                validationError = null
              },
              label = { Text("Password") },
              singleLine = true,
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = SteelSilver
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_password_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = CharcoalBorder,
                focusedLabelColor = MetallicGoldLight,
                unfocusedLabelColor = SteelSilver,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = MetallicGold
              )
            )

            if (validationError != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = validationError ?: "",
                color = Color(0xFFFF8A8A),
                style = MaterialTheme.typography.bodySmall
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct Sign Up / Create Account Action
            Button(
              onClick = {
                if (fullName.trim().isEmpty()) {
                  validationError = "कृपया अपना पूरा नाम दर्ज करें"
                } else if (emailOrPhone.trim().isEmpty()) {
                  validationError = "कृपया Email या Phone नंबर दर्ज करें"
                } else if (password.trim().isEmpty()) {
                  validationError = "कृपया Password दर्ज करें"
                } else {
                  Toast.makeText(context, "खाता सफलतापूर्वक बनाया गया! Welcome to Aravalli.", Toast.LENGTH_SHORT).show()
                  onAuthSuccess()
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("signup_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MetallicGold,
                contentColor = CharcoalDark
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // "OR" Divider
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            HorizontalDivider(
              modifier = Modifier.weight(1f),
              color = CharcoalBorder
            )
            Text(
              text = "OR",
              style = MaterialTheme.typography.labelSmall.copy(
                color = SteelSilver,
                fontWeight = FontWeight.SemiBold
              ),
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(
              modifier = Modifier.weight(1f),
              color = CharcoalBorder
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 3. Google Sign-In Button
          Button(
            onClick = {
              validationError = null
              viewModel.signInWithGoogle(context)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("google_sign_in_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.White,
              contentColor = Color(0xFF1F1F1F)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = uiState !is AuthUiState.Loading
          ) {
            if (uiState is AuthUiState.Loading) {
              CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color(0xFF1F1F1F),
                strokeWidth = 2.dp
              )
            } else {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                GoogleLogo(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = if (selectedTab == 0) "Sign in with Google" else "Sign up with Google",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F1F1F)
                  )
                )
              }
            }
          }
        }
      }

      // Error Message Display with 1-tap Bypass and SHA-1 Helper
      if (uiState is AuthUiState.Error) {
        val error = uiState as AuthUiState.Error
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_error_card"),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1515)),
          shape = RoundedCornerShape(14.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFFFF5252), Color(0xFF882020)))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = error.message,
                color = Color(0xFFFF8A8A),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { viewModel.clearError() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Dismiss error",
                  tint = Color(0xFFFF8A8A),
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1-Tap button to enter app immediately without getting stuck
            Button(
              onClick = {
                viewModel.clearError()
                onAuthSuccess()
              },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(
                containerColor = MetallicGold,
                contentColor = CharcoalDark
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "बिना गूगल के अंदर जाएं (Continue to App)",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Toggle SHA-1 Details
            TextButton(
              onClick = { showShaGuide = !showShaGuide },
              modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = MetallicGoldLight, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (showShaGuide) "Firebase सेटअप बंद करें" else "Firebase SHA-1 व सेटअप देखें",
                  color = MetallicGoldLight,
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }

            if (showShaGuide) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Google Sign-In चालू करने के लिए Firebase Console में यह SHA-1 Fingerprint Add करें:",
                style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver, fontSize = 11.sp)
              )
              Spacer(modifier = Modifier.height(6.dp))

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CharcoalDark,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = AuthViewModel.APP_SHA1,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp
                    ),
                    modifier = Modifier.weight(1f)
                  )
                  OutlinedButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      clipboard.setPrimaryClip(ClipData.newPlainText("SHA1", AuthViewModel.APP_SHA1))
                      Toast.makeText(context, "SHA-1 कॉपी हो गया!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 10.sp, color = MetallicGold)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Direct entry link
      TextButton(
        onClick = onAuthSuccess,
        modifier = Modifier.testTag("offline_demo_button")
      ) {
        Text(
          text = "Explore App as Guest / Partner (सीधे अंदर जाएं)",
          style = MaterialTheme.typography.bodySmall.copy(
            color = SteelSilver.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
          )
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

/**
 * Clean Canvas-based Google "G" logo
 */
@Composable
private fun GoogleLogo(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val stroke = 3.dp.toPx()
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    // Red arc (Top)
    drawArc(
      color = Color(0xFFEA4335),
      startAngle = 200f,
      sweepAngle = 120f,
      useCenter = false,
      style = Stroke(stroke, cap = StrokeCap.Round)
    )

    // Yellow arc (Bottom-Left)
    drawArc(
      color = Color(0xFFFBBC05),
      startAngle = 120f,
      sweepAngle = 80f,
      useCenter = false,
      style = Stroke(stroke, cap = StrokeCap.Round)
    )

    // Green arc (Bottom-Right)
    drawArc(
      color = Color(0xFF34A853),
      startAngle = 45f,
      sweepAngle = 75f,
      useCenter = false,
      style = Stroke(stroke, cap = StrokeCap.Round)
    )

    // Blue arc (Top-Right)
    drawArc(
      color = Color(0xFF4285F4),
      startAngle = 315f,
      sweepAngle = 90f,
      useCenter = false,
      style = Stroke(stroke, cap = StrokeCap.Round)
    )

    // Blue horizontal bar
    drawLine(
      color = Color(0xFF4285F4),
      start = Offset(center.x, center.y),
      end = Offset(center.x + radius * 0.9f, center.y),
      strokeWidth = stroke,
      cap = StrokeCap.Round
    )
  }
}
