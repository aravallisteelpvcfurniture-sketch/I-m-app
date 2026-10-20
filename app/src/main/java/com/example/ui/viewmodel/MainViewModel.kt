package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CatalogueItem
import com.example.data.model.Customer
import com.example.data.model.OrderItem
import com.example.data.model.PaymentRecord
import com.example.data.model.Quotation
import com.example.data.model.SiteMeasurement
import com.example.data.repository.AravalliRepository
import com.example.util.PdfInvoiceGenerator
import com.example.util.RateCalculationInput
import com.example.util.RateCalculationResult
import com.example.util.RateCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
  private val repository: AravalliRepository
) : ViewModel() {

  // Realtime Flows from Repository
  val customers: StateFlow<List<Customer>> = repository.observeCustomers()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val measurements: StateFlow<List<SiteMeasurement>> = repository.observeMeasurements()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val quotations: StateFlow<List<Quotation>> = repository.observeQuotations()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val orders: StateFlow<List<OrderItem>> = repository.observeOrders()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val payments: StateFlow<List<PaymentRecord>> = repository.observePayments()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val catalogueItems: StateFlow<List<CatalogueItem>> = repository.observeCatalogue()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Rate Calculator State
  private val _calculatorInput = MutableStateFlow(RateCalculator.presetTemplates[0])
  val calculatorInput: StateFlow<RateCalculationInput> = _calculatorInput.asStateFlow()

  private val _calculatorResult = MutableStateFlow(RateCalculator.calculate(RateCalculator.presetTemplates[0]))
  val calculatorResult: StateFlow<RateCalculationResult> = _calculatorResult.asStateFlow()

  // Status message / toast
  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  init {
    viewModelScope.launch {
      // Seed initial showroom data if none exists
      repository.observeCatalogue().collect { list ->
        if (list.isEmpty()) {
          repository.seedInitialShowroomData()
        }
      }
    }
  }

  fun updateCalculatorInput(input: RateCalculationInput) {
    _calculatorInput.value = input
    _calculatorResult.value = RateCalculator.calculate(input)
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun addCustomer(
    name: String,
    phone: String,
    siteAddress: String,
    city: String = "Udaipur",
    email: String = "",
    notes: String = "",
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val customer = Customer(
        name = name,
        phone = phone,
        siteAddress = siteAddress,
        city = city,
        email = email,
        notes = notes
      )
      val result = repository.addCustomer(customer)
      if (result.isSuccess) {
        _statusMessage.value = "Customer $name added successfully"
        onSuccess()
      } else {
        _statusMessage.value = "Failed to add customer: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun addMeasurement(
    customerName: String,
    customerPhone: String,
    siteAddress: String,
    category: String,
    length: Double,
    width: Double,
    height: Double,
    quantity: Double,
    unit: String,
    notes: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val measurement = SiteMeasurement(
        customerName = customerName,
        customerPhone = customerPhone,
        siteAddress = siteAddress,
        category = category,
        length = length,
        width = width,
        height = height,
        quantity = quantity,
        unit = unit,
        notes = notes
      )
      val result = repository.addMeasurement(measurement)
      if (result.isSuccess) {
        _statusMessage.value = "Measurement for $customerName saved"
        onSuccess()
      } else {
        _statusMessage.value = "Failed to save measurement: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun createQuotation(
    customerName: String,
    customerPhone: String,
    siteAddress: String,
    category: String,
    subtotal: Double,
    discountAmount: Double,
    taxPercent: Double,
    advanceAmount: Double,
    terms: String,
    isInvoice: Boolean = false,
    onSuccess: (Quotation) -> Unit = {}
  ) {
    viewModelScope.launch {
      val timestamp = SimpleDateFormat("yyMMdd-HHmm", Locale.getDefault()).format(Date())
      val quoteNumber = if (isInvoice) "INV-$timestamp" else "AS-$timestamp"
      val netBeforeTax = subtotal - discountAmount
      val taxAmount = (netBeforeTax * taxPercent) / 100.0
      val total = netBeforeTax + taxAmount
      val balance = total - advanceAmount

      val quote = Quotation(
        quotationNumber = quoteNumber,
        customerName = customerName,
        customerPhone = customerPhone,
        siteAddress = siteAddress,
        category = category,
        subtotal = subtotal,
        discountAmount = discountAmount,
        taxPercent = taxPercent,
        taxAmount = taxAmount,
        totalAmount = total,
        advanceAmount = advanceAmount,
        balanceAmount = balance,
        terms = terms,
        status = if (isInvoice) "CONVERTED_INVOICE" else "SENT",
        isInvoice = isInvoice
      )

      val result = repository.addQuotation(quote)
      if (result.isSuccess) {
        val createdId = result.getOrThrow()
        _statusMessage.value = "${if (isInvoice) "Invoice" else "Quotation"} #$quoteNumber created"
        onSuccess(quote.copy(id = createdId))

        // Also create matching Order entry for workflow tracking
        val order = OrderItem(
          orderNumber = "ORD-$timestamp",
          customerName = customerName,
          customerPhone = customerPhone,
          siteAddress = siteAddress,
          category = category,
          totalAmount = total,
          advancePaid = advanceAmount,
          remainingBalance = balance,
          status = if (isInvoice) "APPROVED" else "ESTIMATE_SENT"
        )
        repository.addOrder(order)
      } else {
        _statusMessage.value = "Failed to create document: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun convertQuotationToInvoice(quotation: Quotation, context: Context) {
    viewModelScope.launch {
      val result = repository.updateQuotationStatus(quotation.id, "CONVERTED_INVOICE", isInvoice = true)
      if (result.isSuccess) {
        _statusMessage.value = "Estimate #${quotation.quotationNumber} converted to Tax Invoice"
        // Generate PDF
        PdfInvoiceGenerator.generateAndSharePdf(
          context = context,
          quotation = quotation.copy(isInvoice = true),
          items = emptyList(),
          isInvoice = true,
          shareViaWhatsApp = false
        )
      }
    }
  }

  fun updateOrderStatus(orderId: String, newStatus: String) {
    viewModelScope.launch {
      val result = repository.updateOrderStatus(orderId, newStatus)
      if (result.isSuccess) {
        _statusMessage.value = "Order status updated to $newStatus"
      }
    }
  }

  fun recordPayment(
    orderNumber: String,
    customerName: String,
    amount: Double,
    paymentMode: String,
    transactionRef: String,
    notes: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val payment = PaymentRecord(
        orderNumber = orderNumber,
        customerName = customerName,
        amount = amount,
        paymentMode = paymentMode,
        transactionRef = transactionRef,
        notes = notes
      )
      val result = repository.recordPayment(payment)
      if (result.isSuccess) {
        _statusMessage.value = "Payment of ₹$amount recorded"
        onSuccess()
      } else {
        _statusMessage.value = "Failed to record payment: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun addCatalogueItem(
    title: String,
    category: String,
    description: String,
    materialSpecs: String,
    dimensions: String,
    baseRate: Double,
    unit: String,
    imageUrl: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val item = CatalogueItem(
        title = title,
        category = category,
        description = description,
        materialSpecs = materialSpecs,
        dimensions = dimensions,
        baseRate = baseRate,
        unit = unit,
        imageUrl = imageUrl,
        featured = true
      )
      val result = repository.addCatalogueItem(item)
      if (result.isSuccess) {
        _statusMessage.value = "Product '$title' added to catalogue"
        onSuccess()
      } else {
        _statusMessage.value = "Failed to add product: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  fun seedSampleData() {
    viewModelScope.launch {
      repository.seedInitialShowroomData()
      _statusMessage.value = "Showroom catalogue re-seeded with realistic models"
    }
  }
}
