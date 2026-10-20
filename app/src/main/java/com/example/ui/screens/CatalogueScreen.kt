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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CatalogueItem
import com.example.data.model.UserRole
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldLight
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SteelSilver
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CatalogueScreen(
  authViewModel: AuthViewModel,
  mainViewModel: MainViewModel,
  onOpenCalculator: () -> Unit
) {
  val selectedRole by authViewModel.selectedRole.collectAsState()
  val catalogueItems by mainViewModel.catalogueItems.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var showAddDialog by remember { mutableStateOf(false) }

  val categories = listOf(
    "All",
    "Stainless Steel Railing",
    "Aluminium Glass Balcony",
    "Wooden Balcony",
    "PVC Furniture",
    "Wardrobes",
    "TV Units",
    "Kitchen Cabinets",
    "Interiors"
  )

  val filteredItems = catalogueItems.filter { item ->
    (selectedCategory == "All" || item.category == selectedCategory) &&
      (item.title.contains(searchQuery, ignoreCase = true) ||
        item.description.contains(searchQuery, ignoreCase = true) ||
        item.materialSpecs.contains(searchQuery, ignoreCase = true))
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CharcoalDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
      Text(
        text = "Product & Design Portfolio",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MetallicGold
        )
      )
      Text(
        text = "Architectural Railings, Glass Balconies, PVC Furniture & Modular Interiors",
        style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("catalogue_search_bar"),
        placeholder = { Text("Search railings, glass, PVC wardrobes, kitchens...") },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = MetallicGold)
        },
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

      Spacer(modifier = Modifier.height(12.dp))

      // Category Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MetallicGold,
              selectedLabelColor = CharcoalDark,
              containerColor = CharcoalSurface,
              labelColor = SteelSilver
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selectedCategory == cat,
              borderColor = if (selectedCategory == cat) MetallicGold else CharcoalBorder
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (filteredItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Architecture,
              contentDescription = null,
              tint = SteelSilver,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No design items match your criteria",
              style = MaterialTheme.typography.bodyMedium.copy(color = SteelSilver)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { mainViewModel.seedSampleData() },
              colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
            ) {
              Text("Load Showroom Designs")
            }
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(filteredItems) { item ->
            CatalogueItemCard(item = item, onCalculateRate = onOpenCalculator)
          }
        }
      }
    }

    // Admin FAB to add new items
    if (selectedRole == UserRole.ADMIN) {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MetallicGold,
        contentColor = CharcoalDark,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("fab_add_catalogue_item")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Product")
      }
    }
  }

  // Add Item Dialog
  if (showAddDialog) {
    AddCatalogueItemDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { title, cat, desc, specs, dims, rate, unit, imgUrl ->
        mainViewModel.addCatalogueItem(title, cat, desc, specs, dims, rate, unit, imgUrl)
        showAddDialog = false
      }
    )
  }
}

@Composable
private fun CatalogueItemCard(
  item: CatalogueItem,
  onCalculateRate: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CharcoalBorder, Color.Transparent)))
  ) {
    Column {
      if (item.imageUrl.isNotBlank()) {
        AsyncImage(
          model = item.imageUrl,
          contentDescription = item.title,
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
          contentScale = ContentScale.Crop
        )
      }

      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MetallicGold.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = item.category,
              style = MaterialTheme.typography.labelSmall.copy(
                color = MetallicGold,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Text(
            text = "₹${item.baseRate.toInt()} ${item.unit}",
            style = MaterialTheme.typography.titleMedium.copy(
              color = MetallicGoldLight,
              fontWeight = FontWeight.ExtraBold
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = item.title,
          style = MaterialTheme.typography.titleMedium.copy(
            color = OffWhite,
            fontWeight = FontWeight.Bold
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = item.description,
          style = MaterialTheme.typography.bodySmall.copy(color = SteelSilver),
          lineHeight = 18.sp
        )

        if (item.materialSpecs.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Specs: ${item.materialSpecs}",
            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold.copy(alpha = 0.8f))
          )
        }

        if (item.dimensions.isNotBlank()) {
          Text(
            text = "Size: ${item.dimensions}",
            style = MaterialTheme.typography.labelSmall.copy(color = SteelSilver.copy(alpha = 0.8f))
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onCalculateRate,
            colors = ButtonDefaults.buttonColors(
              containerColor = MetallicGold,
              contentColor = CharcoalDark
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Estimate Pricing")
          }
        }
      }
    }
  }
}

@Composable
private fun AddCatalogueItemDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, String, Double, String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Stainless Steel Railing") }
  var description by remember { mutableStateOf("") }
  var materialSpecs by remember { mutableStateOf("") }
  var dimensions by remember { mutableStateOf("") }
  var rateStr by remember { mutableStateOf("850") }
  var unit by remember { mutableStateOf("per Rft") }
  var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CharcoalCard,
    title = {
      Text("Add Showroom Product", color = MetallicGold, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Product / Work Title") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Category") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = rateStr,
          onValueChange = { rateStr = it },
          label = { Text("Base Rate (₹)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = unit,
          onValueChange = { unit = it },
          label = { Text("Unit (per Rft / Sq Ft)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = materialSpecs,
          onValueChange = { materialSpecs = it },
          label = { Text("Material Specs") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val rate = rateStr.toDoubleOrNull() ?: 0.0
          if (title.isNotBlank()) {
            onConfirm(title, category, description, materialSpecs, dimensions, rate, unit, imageUrl)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold, contentColor = CharcoalDark)
      ) {
        Text("Save Item")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SteelSilver)
      }
    }
  )
}
