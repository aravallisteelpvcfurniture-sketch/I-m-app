package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.OperationType
import com.example.data.handleFirestoreError
import com.example.data.model.CatalogueItem
import com.example.data.model.Customer
import com.example.data.model.OrderItem
import com.example.data.model.PaymentRecord
import com.example.data.model.Quotation
import com.example.data.model.SiteMeasurement
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AravalliRepository(private val db: FirebaseFirestore) {

  // Secondary constructor resolving named database ID from string resource
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth get() = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in before accessing Firestore.")
  }

  // User Profile
  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val path = "users/$userId"
    emitAll(
      db.collection("users").document(userId)
        .snapshots()
        .map { snapshot -> snapshot.toObject(UserProfile::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
          throw error
        }
    )
  }

  suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    return try {
      val data = mapOf(
        "userId" to uid,
        "name" to profile.name,
        "email" to profile.email,
        "phone" to profile.phone,
        "role" to profile.role,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      docRef.set(data).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun updateUserRole(targetUserId: String, newRole: UserRole): Result<Unit> {
    val docRef = db.collection("users").document(targetUserId)
    return try {
      docRef.update(
        mapOf(
          "role" to newRole.name,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  // Customers
  fun observeCustomers(): Flow<List<Customer>> = flow {
    val uid = requireUserId()
    val path = "customers"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(Customer::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addCustomer(customer: Customer): Result<String> {
    val colRef = db.collection("customers")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "name" to customer.name,
        "phone" to customer.phone,
        "email" to customer.email,
        "siteAddress" to customer.siteAddress,
        "city" to customer.city,
        "notes" to customer.notes,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  // Site Measurements
  fun observeMeasurements(): Flow<List<SiteMeasurement>> = flow {
    val uid = requireUserId()
    val path = "measurements"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(SiteMeasurement::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addMeasurement(measurement: SiteMeasurement): Result<String> {
    val colRef = db.collection("measurements")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "customerName" to measurement.customerName,
        "customerPhone" to measurement.customerPhone,
        "siteAddress" to measurement.siteAddress,
        "category" to measurement.category,
        "length" to measurement.length,
        "width" to measurement.width,
        "height" to measurement.height,
        "quantity" to measurement.quantity,
        "unit" to measurement.unit,
        "notes" to measurement.notes,
        "sitePhotoUrl" to measurement.sitePhotoUrl,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  // Quotations & Invoices
  fun observeQuotations(): Flow<List<Quotation>> = flow {
    val uid = requireUserId()
    val path = "quotations"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(Quotation::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addQuotation(quotation: Quotation): Result<String> {
    val colRef = db.collection("quotations")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "quotationNumber" to quotation.quotationNumber,
        "customerName" to quotation.customerName,
        "customerPhone" to quotation.customerPhone,
        "siteAddress" to quotation.siteAddress,
        "category" to quotation.category,
        "subtotal" to quotation.subtotal,
        "discountAmount" to quotation.discountAmount,
        "taxPercent" to quotation.taxPercent,
        "taxAmount" to quotation.taxAmount,
        "totalAmount" to quotation.totalAmount,
        "advanceAmount" to quotation.advanceAmount,
        "balanceAmount" to quotation.balanceAmount,
        "terms" to quotation.terms,
        "status" to quotation.status,
        "isInvoice" to quotation.isInvoice,
        "itemsJson" to quotation.itemsJson,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  suspend fun updateQuotationStatus(quotationId: String, status: String, isInvoice: Boolean): Result<Unit> {
    val colRef = db.collection("quotations").document(quotationId)
    return try {
      colRef.update(
        mapOf(
          "status" to status,
          "isInvoice" to isInvoice,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, colRef.path)
      Result.failure(e)
    }
  }

  // Orders
  fun observeOrders(): Flow<List<OrderItem>> = flow {
    val uid = requireUserId()
    val path = "orders"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(OrderItem::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addOrder(order: OrderItem): Result<String> {
    val colRef = db.collection("orders")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "orderNumber" to order.orderNumber,
        "customerName" to order.customerName,
        "customerPhone" to order.customerPhone,
        "siteAddress" to order.siteAddress,
        "category" to order.category,
        "totalAmount" to order.totalAmount,
        "advancePaid" to order.advancePaid,
        "remainingBalance" to order.remainingBalance,
        "status" to order.status,
        "assignedStaff" to order.assignedStaff,
        "targetCompletionDate" to order.targetCompletionDate,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
    val colRef = db.collection("orders").document(orderId)
    return try {
      colRef.update(
        mapOf(
          "status" to newStatus,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, colRef.path)
      Result.failure(e)
    }
  }

  // Payments
  fun observePayments(): Flow<List<PaymentRecord>> = flow {
    val uid = requireUserId()
    val path = "payments"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(PaymentRecord::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun recordPayment(payment: PaymentRecord): Result<String> {
    val colRef = db.collection("payments")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "orderNumber" to payment.orderNumber,
        "customerName" to payment.customerName,
        "amount" to payment.amount,
        "paymentMode" to payment.paymentMode,
        "transactionRef" to payment.transactionRef,
        "notes" to payment.notes,
        "createdAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  // Catalogue
  fun observeCatalogue(): Flow<List<CatalogueItem>> = flow {
    val path = "catalogue_items"
    emitAll(
      db.collection(path)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(CatalogueItem::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addCatalogueItem(item: CatalogueItem): Result<String> {
    val colRef = db.collection("catalogue_items")
    return try {
      val uid = requireUserId()
      val payload = mapOf(
        "userId" to uid,
        "title" to item.title,
        "category" to item.category,
        "description" to item.description,
        "materialSpecs" to item.materialSpecs,
        "dimensions" to item.dimensions,
        "baseRate" to item.baseRate,
        "unit" to item.unit,
        "imageUrl" to item.imageUrl,
        "featured" to item.featured,
        "createdAt" to FieldValue.serverTimestamp()
      )
      val doc = colRef.add(payload).await()
      Result.success(doc.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, colRef.path)
      Result.failure(e)
    }
  }

  // Pre-seed realistic sample data for showroom portfolio
  suspend fun seedInitialShowroomData(): Result<Unit> {
    return try {
      val uid = requireUserId()
    val items = listOf(
      CatalogueItem(
        title = "Jindal SS 304 Balcony Glass Railing",
        category = "Stainless Steel Railing",
        description = "High-tensile Grade 304 Stainless Steel Balcony Railing with 12mm Toughened Clear Glass, top square pipe handrail and heavy duty spigot clamps.",
        materialSpecs = "SS 304 Grade, 50x50mm Top Pipe, 12mm Toughened Glass",
        dimensions = "Height: 3.5 Feet | Length: Customizable",
        baseRate = 1250.0,
        unit = "per Rft",
        imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&auto=format&fit=crop&q=80",
        featured = true
      ),
      CatalogueItem(
        title = "Continuous Aluminium Slim Profile Glass Balcony",
        category = "Aluminium Glass Balcony",
        description = "Sleek frameless floor base shoe track profile in architectural powder coated champagne gold / charcoal grey aluminium with laminated double safety glass.",
        materialSpecs = "Heavy Aluminium Channel 80x120mm, PVB Laminated Glass 13.52mm",
        dimensions = "Height: 4 Feet",
        baseRate = 1650.0,
        unit = "per Rft",
        imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=600&auto=format&fit=crop&q=80",
        featured = true
      ),
      CatalogueItem(
        title = "WPC Composite Wooden Deck Balcony Railing",
        category = "Wooden Balcony",
        description = "Weather-resistant, termite-proof exterior WPC fluted louvers paired with black matte stainless steel vertical stanchions for rustic elevation.",
        materialSpecs = "Exterior Grade WPC Composite Panels, SS 304 Matte Black frame",
        dimensions = "Height: 3.5 Feet",
        baseRate = 980.0,
        unit = "per Rft",
        imageUrl = "https://images.unsplash.com/photo-1541123437800-1bb1317badc2?w=600&auto=format&fit=crop&q=80",
        featured = false
      ),
      CatalogueItem(
        title = "Waterproof PVC Modular Kitchen with Soft-Close Baskets",
        category = "Kitchen Cabinets",
        description = "100% termite proof, borer proof, waterproof high-density PVC foam board modular kitchen cabinets with acrylic glossy shutters and Ebco/Hettich tandem boxes.",
        materialSpecs = "18mm High-Density PVC Foam Board, 1mm High-Gloss Acrylic, SS 304 Wire Baskets",
        dimensions = "Customized per Kitchen Layout (L-Shape / U-Shape / Parallel)",
        baseRate = 1450.0,
        unit = "per Sq Ft",
        imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=600&auto=format&fit=crop&q=80",
        featured = true
      ),
      CatalogueItem(
        title = "PVC 4-Door Wardrobe with Loft & Concealed Drawers",
        category = "Wardrobes",
        description = "Heavy PVC carcass wardrobe with dual-tone PU laminate finish, internal organizers, safety locker compartment, and soft-closing hydraulic hinges.",
        materialSpecs = "18mm Waterproof PVC Board, Telescopic Channels, Charcoal Grey & Champagne Gold Trims",
        dimensions = "7 Ft Height x 6 Ft Width x 2 Ft Depth",
        baseRate = 1100.0,
        unit = "per Sq Ft",
        imageUrl = "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&auto=format&fit=crop&q=80",
        featured = true
      ),
      CatalogueItem(
        title = "Floating TV Unit with Charcoal Fluted Louver Panels",
        category = "TV Units",
        description = "Wall-mounted luxury media entertainment console with ambient LED profile backlighting, marble acrylic sheet backdrop, and PVC storage drawers.",
        materialSpecs = "PVC Carcass, Charcoal Fluted Louvers, Warm White 3000K LED Strip, Acrylic Sheet",
        dimensions = "6 Ft Width x 5 Ft Height",
        baseRate = 850.0,
        unit = "per Sq Ft",
        imageUrl = "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=600&auto=format&fit=crop&q=80",
        featured = true
      )
    )

    for (item in items) {
        val payload = mapOf(
          "userId" to uid,
          "title" to item.title,
          "category" to item.category,
          "description" to item.description,
          "materialSpecs" to item.materialSpecs,
          "dimensions" to item.dimensions,
          "baseRate" to item.baseRate,
          "unit" to item.unit,
          "imageUrl" to item.imageUrl,
          "featured" to item.featured,
          "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("catalogue_items").add(payload).await()
      }
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "catalogue_items")
      Result.failure(e)
    }
  }
}
