package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AravalliRepository
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CatalogueScreen
import com.example.ui.screens.CustomerCrmScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarketingPosterScreen
import com.example.ui.screens.MeasurementScreen
import com.example.ui.screens.OrderManagementScreen
import com.example.ui.screens.PaymentLedgerScreen
import com.example.ui.screens.RateCalculatorScreen
import com.example.ui.screens.QuotationScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
  val listener = FirebaseAuth.AuthStateListener { auth ->
    trySend(auth.currentUser)
  }
  addAuthStateListener(listener)
  awaitClose { removeAuthStateListener(listener) }
}

enum class ScreenDestination {
  HOME,
  CATALOGUE,
  CALCULATOR,
  QUOTATIONS,
  MEASUREMENTS,
  ORDERS,
  CRM,
  PAYMENTS,
  MARKETING,
  REPORTS,
  SETTINGS
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true) {
        AppRoot()
      }
    }
  }
}

@Composable
fun AppRoot() {
  val auth = Firebase.auth
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val context = LocalContext.current

  val repository = remember { AravalliRepository(context) }
  val authViewModel = remember { AuthViewModel(repository) }
  var isDemoMode by remember { mutableStateOf(false) }

  if (currentUser == null && !isDemoMode) {
    AuthScreen(
      viewModel = authViewModel,
      onDemoMode = { isDemoMode = true },
      onAuthSuccess = { isDemoMode = true }
    )
  } else {
    val mainViewModel = remember { MainViewModel(repository) }
    MainAppScaffold(
      authViewModel = authViewModel,
      mainViewModel = mainViewModel,
      isDemoMode = isDemoMode && currentUser == null,
      onExitDemoMode = { isDemoMode = false }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
  authViewModel: AuthViewModel,
  mainViewModel: MainViewModel,
  isDemoMode: Boolean = false,
  onExitDemoMode: () -> Unit = {}
) {
  var currentDestination by remember { mutableStateOf(ScreenDestination.HOME) }
  var showMoreSheet by remember { mutableStateOf(false) }

  // Prefill parameters for quotation creation
  var quoteCategory by remember { mutableStateOf("") }
  var quoteSubtotal by remember { mutableDoubleStateOf(0.0) }
  var quoteTotal by remember { mutableDoubleStateOf(0.0) }
  var quoteAdvance by remember { mutableDoubleStateOf(0.0) }

  val snackbarHostState = remember { SnackbarHostState() }
  val statusMessage by mainViewModel.statusMessage.collectAsState()

  LaunchedEffect(statusMessage) {
    statusMessage?.let {
      snackbarHostState.showSnackbar(it)
      mainViewModel.clearStatusMessage()
    }
  }

  // Handle hardware back button to return to HOME from any sub-screen
  BackHandler(enabled = currentDestination != ScreenDestination.HOME) {
    currentDestination = ScreenDestination.HOME
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = CharcoalDark,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      NavigationBar(
        containerColor = CharcoalSurface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = currentDestination == ScreenDestination.HOME,
          onClick = { currentDestination = ScreenDestination.HOME },
          icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CharcoalDark,
            selectedTextColor = MetallicGold,
            indicatorColor = MetallicGold,
            unselectedIconColor = SteelSilver,
            unselectedTextColor = SteelSilver
          ),
          modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
          selected = currentDestination == ScreenDestination.CATALOGUE,
          onClick = { currentDestination = ScreenDestination.CATALOGUE },
          icon = { Icon(Icons.Default.Architecture, contentDescription = "Designs") },
          label = { Text("Designs") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CharcoalDark,
            selectedTextColor = MetallicGold,
            indicatorColor = MetallicGold,
            unselectedIconColor = SteelSilver,
            unselectedTextColor = SteelSilver
          ),
          modifier = Modifier.testTag("nav_designs")
        )

        NavigationBarItem(
          selected = currentDestination == ScreenDestination.CALCULATOR,
          onClick = { currentDestination = ScreenDestination.CALCULATOR },
          icon = { Icon(Icons.Default.Calculate, contentDescription = "Calc") },
          label = { Text("Rates") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CharcoalDark,
            selectedTextColor = MetallicGold,
            indicatorColor = MetallicGold,
            unselectedIconColor = SteelSilver,
            unselectedTextColor = SteelSilver
          ),
          modifier = Modifier.testTag("nav_rates")
        )

        NavigationBarItem(
          selected = currentDestination == ScreenDestination.QUOTATIONS,
          onClick = { currentDestination = ScreenDestination.QUOTATIONS },
          icon = { Icon(Icons.Default.Description, contentDescription = "Quotes") },
          label = { Text("Estimates") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CharcoalDark,
            selectedTextColor = MetallicGold,
            indicatorColor = MetallicGold,
            unselectedIconColor = SteelSilver,
            unselectedTextColor = SteelSilver
          ),
          modifier = Modifier.testTag("nav_estimates")
        )

        NavigationBarItem(
          selected = showMoreSheet,
          onClick = { showMoreSheet = true },
          icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
          label = { Text("More") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CharcoalDark,
            selectedTextColor = MetallicGold,
            indicatorColor = MetallicGold,
            unselectedIconColor = SteelSilver,
            unselectedTextColor = SteelSilver
          ),
          modifier = Modifier.testTag("nav_more")
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isDemoMode) {
        Surface(
          color = MetallicGold.copy(alpha = 0.18f),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onExitDemoMode() }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "⚡ ऑफलाइन पार्टनर मोड (Offline Demo Mode)",
              style = MaterialTheme.typography.labelSmall.copy(color = MetallicGoldLight, fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Google लॉगिन ➔",
              style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
            )
          }
        }
      }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .weight(1f)
      ) {
      when (currentDestination) {
        ScreenDestination.HOME -> HomeScreen(
          authViewModel = authViewModel,
          mainViewModel = mainViewModel,
          onNavigateToEstimates = { currentDestination = ScreenDestination.QUOTATIONS },
          onNavigateToMeasurements = { currentDestination = ScreenDestination.MEASUREMENTS },
          onNavigateToCalculator = { currentDestination = ScreenDestination.CALCULATOR },
          onNavigateToCatalogue = { currentDestination = ScreenDestination.CATALOGUE },
          onNavigateToCustomers = { currentDestination = ScreenDestination.CRM },
          onNavigateToOrders = { currentDestination = ScreenDestination.ORDERS },
          onNavigateToMarketing = { currentDestination = ScreenDestination.MARKETING }
        )

        ScreenDestination.CATALOGUE -> CatalogueScreen(
          authViewModel = authViewModel,
          mainViewModel = mainViewModel,
          onOpenCalculator = { currentDestination = ScreenDestination.CALCULATOR }
        )

        ScreenDestination.CALCULATOR -> RateCalculatorScreen(
          viewModel = mainViewModel,
          onCreateEstimateClicked = { category, subtotal, total, advance ->
            quoteCategory = category
            quoteSubtotal = subtotal
            quoteTotal = total
            quoteAdvance = advance
            currentDestination = ScreenDestination.QUOTATIONS
          }
        )

        ScreenDestination.QUOTATIONS -> QuotationScreen(
          viewModel = mainViewModel,
          prefillCategory = quoteCategory,
          prefillSubtotal = quoteSubtotal,
          prefillTotal = quoteTotal,
          prefillAdvance = quoteAdvance
        )

        ScreenDestination.MEASUREMENTS -> MeasurementScreen(
          viewModel = mainViewModel,
          onGenerateEstimateFromMeasurement = { cust, phone, site, cat ->
            quoteCategory = cat
            quoteSubtotal = 25000.0
            quoteTotal = 29500.0
            quoteAdvance = 14750.0
            currentDestination = ScreenDestination.QUOTATIONS
          }
        )

        ScreenDestination.ORDERS -> OrderManagementScreen(
          viewModel = mainViewModel
        )

        ScreenDestination.CRM -> CustomerCrmScreen(
          viewModel = mainViewModel
        )

        ScreenDestination.PAYMENTS -> PaymentLedgerScreen(
          viewModel = mainViewModel
        )

        ScreenDestination.MARKETING -> MarketingPosterScreen()

        ScreenDestination.REPORTS -> ReportsScreen(
          viewModel = mainViewModel
        )

        ScreenDestination.SETTINGS -> SettingsScreen(
          authViewModel = authViewModel,
          mainViewModel = mainViewModel
        )
      }
    }
  }
}

  // More Navigation BottomSheet
  if (showMoreSheet) {
    ModalBottomSheet(
      onDismissRequest = { showMoreSheet = false },
      containerColor = CharcoalCard,
      sheetState = rememberModalBottomSheetState()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "All Modules & Management",
          style = MaterialTheme.typography.titleMedium.copy(
            color = MetallicGold,
            fontWeight = FontWeight.Bold
          )
        )

        MoreMenuItem(
          icon = Icons.Default.SquareFoot,
          title = "Site Measurements Tracker",
          subtitle = "Log field dimensions, units and site notes",
          onClick = {
            currentDestination = ScreenDestination.MEASUREMENTS
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.ViewList,
          title = "Order Lifecycle & Stages",
          subtitle = "New -> Measurement -> WIP -> Completed",
          onClick = {
            currentDestination = ScreenDestination.ORDERS
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.People,
          title = "Customer Directory & CRM",
          subtitle = "Client contacts, site addresses and click-to-call",
          onClick = {
            currentDestination = ScreenDestination.CRM
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.Payment,
          title = "Payment Ledger & Advances",
          subtitle = "UPI, Cash, Cheque receipts and balance dues",
          onClick = {
            currentDestination = ScreenDestination.PAYMENTS
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.Campaign,
          title = "Festival Posters & Greeting Maker",
          subtitle = "Branded Diwali, Griha Pravesh & Promo posters",
          onClick = {
            currentDestination = ScreenDestination.MARKETING
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.QueryStats,
          title = "Business Reports & Turnover",
          subtitle = "Sales revenue, job completion and category shares",
          onClick = {
            currentDestination = ScreenDestination.REPORTS
            showMoreSheet = false
          }
        )

        MoreMenuItem(
          icon = Icons.Default.Settings,
          title = "Shop Settings & Profile",
          subtitle = "GSTIN, Bank Details, Terms & Role Switching",
          onClick = {
            currentDestination = ScreenDestination.SETTINGS
            showMoreSheet = false
          }
        )

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun MoreMenuItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CharcoalSurface)
      .clickable { onClick() }
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(MetallicGold.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(20.dp))
    }
    Spacer(modifier = Modifier.width(14.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold)
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver)
      )
    }
  }
}
