package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.LineItem
import com.example.data.model.Quotation
import com.example.data.model.ShopInfo
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

  private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

  fun generateAndSharePdf(
    context: Context,
    quotation: Quotation,
    items: List<LineItem>,
    shopInfo: ShopInfo = ShopInfo(),
    isInvoice: Boolean = quotation.isInvoice,
    shareViaWhatsApp: Boolean = false
  ): File? {
    return try {
      val document = PdfDocument()
      val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (72 dpi)
      val page = document.startPage(pageInfo)
      val canvas = page.canvas

      val paint = Paint().apply { isAntiAlias = true }
      val boldPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      // Background Header Bar (Charcoal Black)
      paint.color = Color.rgb(26, 30, 36)
      canvas.drawRect(0f, 0f, 595f, 105f, paint)

      // Gold Accent Line
      paint.color = Color.rgb(212, 175, 55)
      canvas.drawRect(0f, 105f, 595f, 110f, paint)

      // Shop Name & Branding Header
      boldPaint.color = Color.rgb(212, 175, 55)
      boldPaint.textSize = 20f
      canvas.drawText(shopInfo.name.uppercase(Locale.ROOT), 30f, 40f, boldPaint)

      paint.color = Color.WHITE
      paint.textSize = 10f
      canvas.drawText(shopInfo.tagline, 30f, 56f, paint)
      canvas.drawText("${shopInfo.address} | Tel: ${shopInfo.phone}", 30f, 72f, paint)
      canvas.drawText("GSTIN: ${shopInfo.gstin} | Email: ${shopInfo.email}", 30f, 88f, paint)

      // Document Badge (Quotation or Tax Invoice)
      val docTitle = if (isInvoice) "TAX INVOICE" else "ESTIMATE / QUOTATION"
      boldPaint.color = if (isInvoice) Color.rgb(46, 125, 50) else Color.rgb(212, 175, 55)
      boldPaint.textSize = 16f
      canvas.drawText(docTitle, 390f, 50f, boldPaint)

      paint.color = Color.WHITE
      paint.textSize = 10f
      val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
      canvas.drawText("Doc #: ${quotation.quotationNumber}", 390f, 70f, paint)
      canvas.drawText("Date: $dateStr", 390f, 86f, paint)

      // Customer Details Card
      paint.color = Color.rgb(245, 246, 248)
      canvas.drawRoundRect(30f, 125f, 565f, 195f, 8f, 8f, paint)

      boldPaint.color = Color.rgb(30, 35, 42)
      boldPaint.textSize = 11f
      canvas.drawText("CUSTOMER & SITE DETAILS:", 45f, 145f, boldPaint)

      paint.color = Color.rgb(60, 64, 70)
      paint.textSize = 10f
      canvas.drawText("Name: ${quotation.customerName}", 45f, 162f, paint)
      canvas.drawText("Phone: ${quotation.customerPhone}", 45f, 178f, paint)
      canvas.drawText("Site Location: ${quotation.siteAddress}", 300f, 162f, paint)
      canvas.drawText("Project Category: ${quotation.category}", 300f, 178f, paint)

      // Table Header
      paint.color = Color.rgb(35, 40, 50)
      canvas.drawRect(30f, 210f, 565f, 235f, paint)

      boldPaint.color = Color.rgb(212, 175, 55)
      boldPaint.textSize = 10f
      canvas.drawText("SR", 40f, 227f, boldPaint)
      canvas.drawText("ITEM DESCRIPTION & SPECIFICATIONS", 75f, 227f, boldPaint)
      canvas.drawText("QTY", 365f, 227f, boldPaint)
      canvas.drawText("RATE (₹)", 425f, 227f, boldPaint)
      canvas.drawText("AMOUNT (₹)", 495f, 227f, boldPaint)

      // Table Rows
      var y = 255f
      val effectiveItems = if (items.isNotEmpty()) items else listOf(
        LineItem(
          description = "${quotation.category} - Materials & Installation",
          quantity = 1.0,
          unit = "Lot",
          rate = quotation.subtotal,
          total = quotation.subtotal
        )
      )

      paint.color = Color.rgb(40, 44, 52)
      paint.textSize = 9.5f

      for ((index, item) in effectiveItems.withIndex()) {
        if (index % 2 == 1) {
          val rowBg = Paint().apply { color = Color.rgb(250, 250, 252) }
          canvas.drawRect(30f, y - 14f, 565f, y + 6f, rowBg)
        }

        canvas.drawText("${index + 1}", 40f, y, paint)
        canvas.drawText(item.description.take(45), 75f, y, paint)
        canvas.drawText("${item.quantity} ${item.unit}", 365f, y, paint)
        canvas.drawText(String.format(Locale.US, "%.2f", item.rate), 425f, y, paint)
        canvas.drawText(String.format(Locale.US, "%.2f", item.total), 495f, y, paint)
        y += 22f
      }

      // Divider
      paint.color = Color.rgb(200, 205, 215)
      paint.strokeWidth = 1f
      canvas.drawLine(30f, y + 5f, 565f, y + 5f, paint)

      // Totals & Financial Breakdown
      val summaryY = y + 25f
      paint.color = Color.rgb(70, 75, 85)
      paint.textSize = 10f
      boldPaint.textSize = 10f
      boldPaint.color = Color.rgb(30, 35, 42)

      canvas.drawText("Subtotal:", 380f, summaryY, paint)
      canvas.drawText(currencyFormatter.format(quotation.subtotal), 480f, summaryY, boldPaint)

      if (quotation.discountAmount > 0) {
        canvas.drawText("Discount:", 380f, summaryY + 18f, paint)
        canvas.drawText("- " + currencyFormatter.format(quotation.discountAmount), 480f, summaryY + 18f, boldPaint)
      }

      canvas.drawText("GST (${quotation.taxPercent}%):", 380f, summaryY + 36f, paint)
      canvas.drawText(currencyFormatter.format(quotation.taxAmount), 480f, summaryY + 36f, boldPaint)

      // Total Box
      paint.color = Color.rgb(26, 30, 36)
      canvas.drawRoundRect(360f, summaryY + 48f, 565f, summaryY + 78f, 6f, 6f, paint)
      boldPaint.color = Color.rgb(212, 175, 55)
      boldPaint.textSize = 12f
      canvas.drawText("FINAL TOTAL:", 375f, summaryY + 68f, boldPaint)
      canvas.drawText(currencyFormatter.format(quotation.totalAmount), 465f, summaryY + 68f, boldPaint)

      // Advance & Balance
      boldPaint.color = Color.rgb(46, 125, 50)
      boldPaint.textSize = 10f
      canvas.drawText("Advance Required/Paid: ${currencyFormatter.format(quotation.advanceAmount)}", 380f, summaryY + 98f, boldPaint)
      boldPaint.color = Color.rgb(198, 40, 40)
      canvas.drawText("Balance Remaining: ${currencyFormatter.format(quotation.balanceAmount)}", 380f, summaryY + 114f, boldPaint)

      // Terms & Bank Details (Left side bottom)
      boldPaint.color = Color.rgb(30, 35, 42)
      boldPaint.textSize = 9.5f
      canvas.drawText("TERMS & CONDITIONS:", 30f, summaryY, boldPaint)

      paint.color = Color.rgb(80, 85, 95)
      paint.textSize = 8.5f
      val termsLines = quotation.terms.split("\n")
      var termY = summaryY + 16f
      for (line in termsLines.take(5)) {
        canvas.drawText(line.take(65), 30f, termY, paint)
        termY += 13f
      }

      // Bank Details for NEFT / UPI
      boldPaint.color = Color.rgb(30, 35, 42)
      boldPaint.textSize = 9.5f
      canvas.drawText("PAYMENT DETAILS (NEFT / RTGS / UPI):", 30f, termY + 15f, boldPaint)
      paint.textSize = 8.5f
      canvas.drawText("Bank: ${shopInfo.bankName} | A/C: ${shopInfo.accountNumber}", 30f, termY + 28f, paint)
      canvas.drawText("IFSC: ${shopInfo.ifscCode} | UPI ID: ${shopInfo.upiId}", 30f, termY + 41f, paint)

      // Footer & Authorized Signatory
      paint.color = Color.rgb(200, 205, 215)
      canvas.drawLine(30f, 780f, 565f, 780f, paint)

      paint.color = Color.rgb(100, 105, 115)
      paint.textSize = 8f
      canvas.drawText("Thank you for your business! This is a system-generated document from Aravalli Steel App.", 30f, 800f, paint)

      boldPaint.color = Color.rgb(30, 35, 42)
      boldPaint.textSize = 9f
      canvas.drawText("For ARAVALLI STEEL & PVC FURNITURE", 390f, 795f, boldPaint)
      canvas.drawText("(Authorized Signatory)", 420f, 825f, paint)

      document.finishPage(page)

      // Save PDF to cache directory
      val docsFolder = File(context.cacheDir, "docs")
      if (!docsFolder.exists()) docsFolder.mkdirs()

      val fileName = "${quotation.quotationNumber.replace("/", "_")}.pdf"
      val file = File(docsFolder, fileName)
      val fos = FileOutputStream(file)
      document.writeTo(fos)
      fos.flush()
      fos.close()
      document.close()

      // Share via Android Native Share Sheet or direct WhatsApp
      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "com.aravalli.steelpvcfurniture.fileprovider",
        file
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "$docTitle - ${quotation.quotationNumber} - Aravalli Steel")
        putExtra(
          Intent.EXTRA_TEXT,
          "Namaste ${quotation.customerName},\nPlease find attached your $docTitle #${quotation.quotationNumber} from Aravalli Steel & PVC Furniture.\nTotal: ${currencyFormatter.format(quotation.totalAmount)}\nFor queries, call us at ${shopInfo.phone}."
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareViaWhatsApp) {
          setPackage("com.whatsapp")
        }
      }

      val chooser = Intent.createChooser(shareIntent, "Share $docTitle PDF via")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)

      file
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Error generating PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
      null
    }
  }

  fun openWhatsAppChat(context: Context, phone: String, message: String) {
    try {
      val cleanPhone = phone.replace(Regex("[^0-9]"), "").let {
        if (it.length == 10) "91$it" else it
      }
      val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "WhatsApp not installed or invalid number", Toast.LENGTH_SHORT).show()
    }
  }

  fun makePhoneCall(context: Context, phone: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Unable to launch dialer", Toast.LENGTH_SHORT).show()
    }
  }
}
