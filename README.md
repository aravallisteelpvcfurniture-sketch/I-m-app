# Aravalli Steel & PVC Furniture - Android Application

A complete, production-ready native Android application built with **Kotlin**, **Jetpack Compose**, **Material 3**, **MVVM architecture**, **Kotlin Coroutines**, and **Firebase** (Firebase Authentication with Jetpack Credential Manager & Cloud Firestore with hardened Zero-Trust Security Rules).

The application is tailored for **Aravalli Steel & PVC Furniture**, providing Stainless Steel Railings (SS 304 / SS 202), Aluminium Glass Balconies, Wooden Composite Deck Balconies, PVC Modular Kitchens, PVC Wardrobes, TV Units, Wall Paneling, and Complete Home Interior Design.

---

## 🌟 Key Application Features

1. **Brand Identity & Premium Charcoal & Gold Theme**:
   - Modern Material 3 dark design system using Charcoal Black (`#121418`), Metallic Gold (`#D4AF37`), Steel Silver (`#B0BEC5`), and crisp contrast whites.
   - Custom adaptive launcher icons and logos.

2. **Secure Firebase Authentication with Role-Based Access Control (RBAC)**:
   - Google Sign-In powered by modern **Jetpack Credential Manager** (`GetSignInWithGoogleOption`).
   - Dynamic Roles: **Admin** (Proprietor/Management), **Staff** (Field Fabricators/Supervisors), and **Customer**.
   - Profile management and role-gated UI actions.

3. **Home Dashboard**:
   - Financial summaries: Total Turnovers, Outstanding Balances, Active Job Count, Site Visits.
   - Quick Actions Grid: New Estimate, Site Measurement, Rate Calculator, Add Customer, Showroom Catalogue, Marketing Posters.
   - Core fabrication work cards and recent orders with stage indicators.

4. **Product & Design Portfolio Catalogue**:
   - 8 Core Categories: Stainless Steel Railing, Aluminium Glass Balcony, Wooden Balcony, PVC Furniture, Wardrobes, TV Units, Modular Kitchens, and Complete Interiors.
   - High-resolution design showcase, dimensions, material specifications, and base rates.
   - Instant "Estimate Pricing" action for any catalogue design.
   - Admin dialog to add and update portfolio items.

5. **Accurate & Configurable Rate Calculator Engine**:
   - Dedicated industry-standard fabrication formulas for:
     - Stainless Steel Railing (SS 304 Jindal vs SS 202, pipe profiles, balusters)
     - Aluminium Glass Balcony (Shoe profiles, 12mm Toughened, 13.52mm Laminated PVB)
     - PVC Furniture (18mm High-Density foam board, glossy acrylic, tandem soft-close)
   - Live calculations itemizing: Raw Material, Glass Infill, Hardware Accessories, Skilled Labour, Transport, GST (18%), and Discounts.
   - Clear fabrication assumptions and instant conversion into an official Quotation.

6. **Itemized Quotations, Tax Invoices & Native PDF Generation**:
   - Automatic unique document numbering (`AS-YYMMDD-HHMM` for Quotations and `INV-YYMMDD-HHMM` for Tax Invoices).
   - Generates official high-resolution A4 PDFs with shop GSTIN, bank details, customer details, itemized table, payment milestones, and authorized signatory box.
   - Native Android Share Sheet and direct WhatsApp share using `FileProvider`.

7. **Site Measurement Module**:
   - Captures customer contact, site address/plot #, work category, length, width, height, quantity, and unit (RFT, SQFT, INCH, MM, METRE).
   - Direct Call and WhatsApp location integration.
   - "Make Quote" action directly from measurement records.

8. **Order Lifecycle & Stage Tracker**:
   - Tracks jobs across 8 stages: `NEW` ➔ `MEASUREMENT` ➔ `ESTIMATE_SENT` ➔ `APPROVED` ➔ `WIP` ➔ `INSTALLATION` ➔ `COMPLETED` ➔ `CANCELLED`.
   - Lead fabricator assignments, target completion dates, and balance dues.

9. **Customer CRM & Site Directory**:
   - Client contact profiles with one-tap Phone Dialing (`tel:`) and WhatsApp direct chat (`wa.me`).
   - Site addresses and project notes.

10. **Payment Ledger & Receipts**:
    - Real-time tracking of advances, interim milestone payments, and remaining balances.
    - Supports Cash, UPI, Bank Transfer (NEFT/RTGS), and Cheque.

11. **Marketing & Festival Poster Creator**:
    - Generates personalized festival greetings (Diwali Mubarak, Griha Pravesh, Factory Direct Offers).
    - Branded with Aravalli Steel & PVC Furniture emblem, phone number, address, and personalized client name for instant WhatsApp status/chat sharing.

12. **Business Reports & Analytics**:
    - Total order bookings, collection ratios, pending dues, completion rates, and category revenue distribution.

13. **Shop Settings & RBAC Switcher**:
    - Firm branding, GSTIN, Bank A/C, IFSC, and UPI details.
    - Role switcher allowing seamless verification of Admin, Staff, and Customer user experiences.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Clean MVVM (Model-View-ViewModel) with unidirectional data flow
- **Concurrency**: Kotlin Coroutines & `StateFlow` / reactive `snapshots()` Flows
- **Authentication**: Firebase Authentication via Jetpack Credential Manager (`GetSignInWithGoogleOption`)
- **Cloud Database**: Cloud Firestore (Enterprise Edition with custom database ID)
- **Security Rules**: Zero-Trust, schema-validated Firestore Security Rules with client query alignment
- **Image Loading**: Coil Compose
- **PDF Generation**: Android Native `PdfDocument` with `FileProvider`

---

## 🚀 Building & Running the Project

### Building in Android Studio (Local Machine)
1. Clone or download this project folder.
2. Open **Android Studio Ladybug** (or later).
3. Ensure JDK 17 or higher is configured (`Settings > Build, Execution, Deployment > Build Tools > Gradle`).
4. Click **Sync Project with Gradle Files**.
5. Connect an Android device with Developer Options & USB Debugging enabled (or start an Android Virtual Device).
6. Click **Run 'app'** or press `Shift + F10`.

### Building the Debug APK via Command Line
Run the standard Gradle assemble command:
```bash
gradle :app:assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Building the APK via GitHub Actions
A complete GitHub Actions workflow is pre-configured in `.github/workflows/android-build.yml`:
1. Push this repository to GitHub.
2. Navigate to the **Actions** tab on your GitHub repository.
3. Select **Build Aravalli Steel Android APK** and click **Run workflow**.
4. Once the build finishes, download the generated **`AravalliSteelPvCFurniture-Debug-APK`** artifact directly from the workflow summary page.

---

## 🛡️ Firebase & Security Rules Configuration

- Firestore security rules are defined in `/firestore.rules` and tested locally using `@firebase/rules-unit-testing` and Robolectric unit tests in `app/src/test/java/`.
- All writes and reads require authenticated user credentials via Google Sign-In.
- Cross-user data isolation ensures user records cannot be manipulated or snooped by unauthorized third parties.
