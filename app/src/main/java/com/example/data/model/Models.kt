package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class UserRole {
  ADMIN,
  STAFF,
  CUSTOMER
}

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val email: String = "",
  val phone: String = "",
  val role: String = UserRole.CUSTOMER.name,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Customer(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val phone: String = "",
  val email: String = "",
  val siteAddress: String = "",
  val city: String = "Udaipur",
  val notes: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SiteMeasurement(
  @DocumentId val id: String = "",
  val userId: String = "",
  val customerName: String = "",
  val customerPhone: String = "",
  val siteAddress: String = "",
  val category: String = "Stainless Steel Railing",
  val length: Double = 0.0,
  val width: Double = 0.0,
  val height: Double = 0.0,
  val quantity: Double = 1.0,
  val unit: String = "RFT", // RFT, SQFT, INCH, MM, METRE
  val notes: String = "",
  val sitePhotoUrl: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Quotation(
  @DocumentId val id: String = "",
  val userId: String = "",
  val quotationNumber: String = "",
  val customerName: String = "",
  val customerPhone: String = "",
  val siteAddress: String = "",
  val category: String = "Stainless Steel Railing",
  val subtotal: Double = 0.0,
  val discountAmount: Double = 0.0,
  val taxPercent: Double = 18.0,
  val taxAmount: Double = 0.0,
  val totalAmount: Double = 0.0,
  val advanceAmount: Double = 0.0,
  val balanceAmount: Double = 0.0,
  val terms: String = "1. 50% advance along with confirmed work order.\n2. 40% on material delivery at site.\n3. 10% balance upon final installation.\n4. Rates inclusive of fabrication & installation.",
  val status: String = "DRAFT", // DRAFT, SENT, ACCEPTED, CONVERTED_INVOICE, CANCELLED
  val isInvoice: Boolean = false,
  val itemsJson: String = "", // JSON list of itemized items
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class OrderItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val orderNumber: String = "",
  val customerName: String = "",
  val customerPhone: String = "",
  val siteAddress: String = "",
  val category: String = "Stainless Steel Railing",
  val totalAmount: Double = 0.0,
  val advancePaid: Double = 0.0,
  val remainingBalance: Double = 0.0,
  val status: String = "NEW", // NEW, MEASUREMENT, ESTIMATE_SENT, APPROVED, WIP, INSTALLATION, COMPLETED, CANCELLED
  val assignedStaff: String = "Unassigned",
  val targetCompletionDate: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PaymentRecord(
  @DocumentId val id: String = "",
  val userId: String = "",
  val orderNumber: String = "",
  val customerName: String = "",
  val amount: Double = 0.0,
  val paymentMode: String = "UPI", // CASH, UPI, BANK_TRANSFER, CHEQUE
  val transactionRef: String = "",
  val notes: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CatalogueItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val category: String = "Stainless Steel Railing",
  val description: String = "",
  val materialSpecs: String = "",
  val dimensions: String = "",
  val baseRate: Double = 0.0,
  val unit: String = "per Rft",
  val imageUrl: String = "",
  val featured: Boolean = true,
  @ServerTimestamp val createdAt: Timestamp? = null
)

data class LineItem(
  val description: String = "",
  val quantity: Double = 1.0,
  val unit: String = "Rft",
  val rate: Double = 0.0,
  val total: Double = 0.0
)

data class ShopInfo(
  val name: String = "Aravalli Steel & PVC Furniture",
  val tagline: String = "Stainless Steel Railings, Glass Balconies, PVC Furniture & Modular Interiors",
  val ownerName: String = "Harish Patel",
  val phone: String = "+91 94140 12345",
  val whatsappPhone: String = "+919414012345",
  val email: String = "contact@aravallisteel.com",
  val address: String = "Plot 18, Near Sukher Industrial Area, NH-8, Udaipur, Rajasthan 313001",
  val gstin: String = "08AABCA1234F1Z5",
  val bankName: String = "State Bank of India",
  val accountNumber: String = "39201928374",
  val ifscCode: String = "SBIN0001234",
  val upiId: String = "aravallisteel@sbi"
)
