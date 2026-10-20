package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Customer
import com.example.data.model.Quotation
import com.example.data.model.SiteMeasurement
import com.example.data.repository.AravalliRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AravalliRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun addCustomer_authenticatedUser_createsCustomerSuccessfully() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = AravalliRepository(firestore)

    val customer = Customer(
      name = "Ramesh Sharma",
      phone = "+919876543210",
      siteAddress = "Sector 14, Hiran Magri",
      city = "Udaipur"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addCustomer(customer)
    }

    assertTrue(result.isSuccess)
    val id = result.getOrThrow()
    assertTrue(id.isNotBlank())
  }

  @Test
  fun observeCustomers_authenticatedUser_returnsCustomers() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = AravalliRepository(firestore)

    val customer = Customer(
      name = "Dinesh Patel",
      phone = "+919811223344",
      siteAddress = "Sukher NH-8",
      city = "Udaipur"
    )
    withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addCustomer(customer)
    }

    val list = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeCustomers().first { it.isNotEmpty() }
    }
    assertTrue(list.any { it.name == "Dinesh Patel" })
  }

  @Test
  fun addCustomer_unauthenticatedUser_fails() = runBlocking {
    auth.signOut()
    val repository = AravalliRepository(firestore)

    val customer = Customer(
      name = "Unauth Customer",
      phone = "+919999999999",
      siteAddress = "Nowhere",
      city = "Udaipur"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addCustomer(customer)
    }

    assertTrue(result.isFailure)
  }

  @Test
  fun addMeasurement_authenticatedUser_createsMeasurement() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = AravalliRepository(firestore)

    val measurement = SiteMeasurement(
      customerName = "Vikram Singh",
      customerPhone = "+919414000111",
      siteAddress = "Saheli Nagar, Udaipur",
      category = "Stainless Steel Railing",
      length = 35.0,
      width = 3.5,
      quantity = 1.0,
      unit = "RFT"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addMeasurement(measurement)
    }

    assertTrue(result.isSuccess)
    val id = result.getOrThrow()
    assertTrue(id.isNotBlank())
  }

  @Test
  fun addQuotation_authenticatedUser_createsQuotation() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = AravalliRepository(firestore)

    val quote = Quotation(
      quotationNumber = "AS-TEST-${UUID.randomUUID().toString().take(6)}",
      customerName = "Manish Joshi",
      customerPhone = "+919829012345",
      siteAddress = "Fatehpura, Udaipur",
      category = "Aluminium Glass Balcony",
      subtotal = 45000.0,
      discountAmount = 2000.0,
      taxPercent = 18.0,
      taxAmount = 7740.0,
      totalAmount = 50740.0,
      advanceAmount = 25000.0,
      balanceAmount = 25740.0
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addQuotation(quote)
    }

    assertTrue(result.isSuccess)
    val id = result.getOrThrow()
    assertTrue(id.isNotBlank())
  }

  private companion object {
    const val ALICE_EMAIL = "alice@aravalli.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
