package com.example.util

data class RateCalculationInput(
  val category: String = "Stainless Steel Railing",
  val subTypeOrGrade: String = "SS 304 Grade",
  val profileType: String = "50mm Round Pipe Handrail",
  val glassType: String = "12mm Toughened Clear Glass",
  val quantityLength: Double = 30.0, // RFT or SQFT
  val unit: String = "RFT",
  val materialRatePerUnit: Double = 850.0,
  val glassRatePerUnit: Double = 350.0,
  val accessoriesCost: Double = 2500.0,
  val labourRatePerUnit: Double = 180.0,
  val transportCost: Double = 1500.0,
  val discountPercent: Double = 5.0,
  val taxPercent: Double = 18.0,
  val advancePercent: Double = 50.0
)

data class RateCalculationResult(
  val materialCost: Double,
  val glassCost: Double,
  val accessoriesCost: Double,
  val labourCost: Double,
  val transportCost: Double,
  val subtotal: Double,
  val discountAmount: Double,
  val netBeforeTax: Double,
  val taxAmount: Double,
  val finalTotal: Double,
  val advanceAmount: Double,
  val balanceAmount: Double,
  val assumptionsText: String
)

object RateCalculator {

  fun calculate(input: RateCalculationInput): RateCalculationResult {
    val materialCost = input.quantityLength * input.materialRatePerUnit
    val glassCost = if (input.glassType.isNotEmpty() && input.glassType != "None") {
      input.quantityLength * input.glassRatePerUnit
    } else 0.0

    val accessoriesCost = input.accessoriesCost
    val labourCost = input.quantityLength * input.labourRatePerUnit
    val transportCost = input.transportCost

    val subtotal = materialCost + glassCost + accessoriesCost + labourCost + transportCost
    val discountAmount = (subtotal * input.discountPercent) / 100.0
    val netBeforeTax = subtotal - discountAmount
    val taxAmount = (netBeforeTax * input.taxPercent) / 100.0
    val finalTotal = netBeforeTax + taxAmount

    val advanceAmount = (finalTotal * input.advancePercent) / 100.0
    val balanceAmount = finalTotal - advanceAmount

    val assumptions = buildString {
      append("• Category: ${input.category} (${input.subTypeOrGrade})\n")
      append("• Profile / Specs: ${input.profileType}\n")
      if (glassCost > 0) append("• Glass Infill: ${input.glassType} @ ₹${input.glassRatePerUnit}/${input.unit}\n")
      append("• Total Dimension: ${input.quantityLength} ${input.unit}\n")
      append("• Material Rate: ₹${input.materialRatePerUnit}/${input.unit} | Labour: ₹${input.labourRatePerUnit}/${input.unit}\n")
      append("• Fabrication Hardware & Anchor accessories: ₹$accessoriesCost | Local Transport: ₹$transportCost\n")
      append("• GST @ ${input.taxPercent}% applicable on commercial invoicing.\n")
      append("• Payment milestone: ${input.advancePercent}% advance, remaining on site installation.")
    }

    return RateCalculationResult(
      materialCost = materialCost,
      glassCost = glassCost,
      accessoriesCost = accessoriesCost,
      labourCost = labourCost,
      transportCost = transportCost,
      subtotal = subtotal,
      discountAmount = discountAmount,
      netBeforeTax = netBeforeTax,
      taxAmount = taxAmount,
      finalTotal = finalTotal,
      advanceAmount = advanceAmount,
      balanceAmount = balanceAmount,
      assumptionsText = assumptions
    )
  }

  // Pre-configured rate defaults for various business products
  val presetTemplates = listOf(
    RateCalculationInput(
      category = "Stainless Steel Railing",
      subTypeOrGrade = "SS 304 Grade (Jindal)",
      profileType = "50mm Round Pipe + 3 SS Balusters",
      glassType = "None",
      quantityLength = 40.0,
      unit = "RFT",
      materialRatePerUnit = 650.0,
      glassRatePerUnit = 0.0,
      accessoriesCost = 1800.0,
      labourRatePerUnit = 160.0,
      transportCost = 1000.0,
      discountPercent = 5.0
    ),
    RateCalculationInput(
      category = "Stainless Steel Railing",
      subTypeOrGrade = "SS 304 with Toughened Glass",
      profileType = "Top SS Handrail + Heavy Spigot Floor Clamps",
      glassType = "12mm Toughened Glass with Polished Edges",
      quantityLength = 35.0,
      unit = "RFT",
      materialRatePerUnit = 750.0,
      glassRatePerUnit = 480.0,
      accessoriesCost = 3500.0,
      labourRatePerUnit = 220.0,
      transportCost = 1500.0,
      discountPercent = 5.0
    ),
    RateCalculationInput(
      category = "Aluminium Glass Balcony",
      subTypeOrGrade = "Architectural Aluminium",
      profileType = "Continuous Floor Base Track (Shoe Profile)",
      glassType = "13.52mm Laminated PVB Safety Glass",
      quantityLength = 25.0,
      unit = "RFT",
      materialRatePerUnit = 950.0,
      glassRatePerUnit = 650.0,
      accessoriesCost = 4200.0,
      labourRatePerUnit = 250.0,
      transportCost = 2000.0,
      discountPercent = 0.0
    ),
    RateCalculationInput(
      category = "Kitchen Cabinets",
      subTypeOrGrade = "18mm Waterproof PVC Foam Board",
      profileType = "High-Gloss Acrylic Shutters + Soft Close Tandem",
      glassType = "Fluted Glass Accent Shutter",
      quantityLength = 65.0,
      unit = "SQFT",
      materialRatePerUnit = 1100.0,
      glassRatePerUnit = 120.0,
      accessoriesCost = 9500.0,
      labourRatePerUnit = 220.0,
      transportCost = 2500.0,
      discountPercent = 7.0
    ),
    RateCalculationInput(
      category = "Wardrobes",
      subTypeOrGrade = "Heavy PVC Carcass with Dual Tone Laminate",
      profileType = "Sliding 2-Door Track with Loft",
      glassType = "Tinted Mirror Door Inset",
      quantityLength = 70.0,
      unit = "SQFT",
      materialRatePerUnit = 900.0,
      glassRatePerUnit = 150.0,
      accessoriesCost = 6500.0,
      labourRatePerUnit = 190.0,
      transportCost = 2000.0,
      discountPercent = 5.0
    ),
    RateCalculationInput(
      category = "TV Units",
      subTypeOrGrade = "PVC Board with Fluted Louver Panels",
      profileType = "Wall Backdrop + Suspended Console Drawers",
      glassType = "None",
      quantityLength = 32.0,
      unit = "SQFT",
      materialRatePerUnit = 680.0,
      glassRatePerUnit = 0.0,
      accessoriesCost = 3000.0,
      labourRatePerUnit = 150.0,
      transportCost = 1200.0,
      discountPercent = 5.0
    )
  )
}
