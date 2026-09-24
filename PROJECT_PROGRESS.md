# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 8 — Authoritative Borderless 15% Radius Logo, UI Overlap Fixes & R8 Update Resolution — COMPLETED

## Current Task
All reported issues from user feedback resolved, built, and verified live:
1. Borderless logo with 15% corner radius container standardized across Android and Windows (.exe, .msi, app.ico, tray.ico, installer.ico).
2. Resolved icon overlap in Android Mouse section (decoupled action buttons with explicit 34dp touch containers and weighted dock navigation).
3. Resolved R8/Gson "Abstract classes can't be instantiated" update error by migrating `UpdateManager.kt` to zero-reflection native `org.json.JSONObject` + comprehensive ProGuard rules.
4. Clean WiX v5 MSI package built and published alongside single-file EXE.
5. Deployed to Firebase Hosting and verified live with `curl.exe -I`.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Authoritative Borderless 15% Logo Pipeline**:
  - `shared/logo.jpg` updated from user-uploaded authoritative artwork (`media_1790216927404.jpg`).
  - Removed all artificial border strokes in `create_tray_badge`, `MainWindow.xaml`, `InstallerWindow.xaml`, and `HomeScreen.kt`.
  - Applied 15% corner radius container across Windows `.ico` assets and Android launcher mipmaps.
- [x] **CHECKPOINT 02 — Android Mouse Section Header Overlap Fix**:
  - Replaced Material3 `IconButton` (which enforces 48dp min touch constraints causing collision) with clean 34dp circular Box containers with `Arrangement.spacedBy(8.dp)`.
  - Added `Modifier.weight(1f, fill = false)` and single-line text truncation to host pill.
  - Added `Modifier.weight(1f)` to each bottom dock item to prevent navbar overlap on any screen width.
- [x] **CHECKPOINT 03 — R8 / Gson Update Failure Resolution**:
  - Replaced Gson in `UpdateManager.kt` with Android runtime `org.json.JSONObject` (zero reflection, immune to R8 minification/obfuscation).
  - Added `-keep class com.knowthemice.app.update.** { *; }` and Gson ProGuard rules to `proguard-rules.pro`.
  - Updated Android `versionCode = 3`, `versionName = "1.2.0"`.
- [x] **CHECKPOINT 04 — Dual Windows Packaging (EXE + WiX MSI)**:
  - Built `KnowTheMice-Setup-x64.exe` (1,439,912 bytes, SHA-256: `A6F24FBE9295B55FEF015D7869F4887C7BF576CAA0A005EF06E4AAFD8026F17D`).
  - Built `KnowTheMice-Setup-x64.msi` (225,280 bytes, SHA-256: `8BD295F7E1DE401DCF4C48447EBF1B1593AF64F2670FC8A12ACA4C721D2C7AD6`).
- [x] **CHECKPOINT 05 — Production Deployment & Live Verification**:
  - Built `KnowTheMice-Android.apk` (2,707,506 bytes, SHA-256: `1CC08EDFD0950E68D8F93F7D86A5EC2ED330C8658AC85CFE1101D42661A73B9B`).
  - Updated `downloads.json` and `Download.tsx`.
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Live HTTP 200 verified on all binary and manifest endpoints.

---

## Last Verified State
1. Windows EXE & WiX MSI: Built and verified with 0 errors.
2. Android Release APK: Minified with R8, signed, and assembled with 0 errors.
3. Live Endpoints:
   - `https://knowthemice.web.app/downloads.json` (200 OK)
   - `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` (200 OK)
   - `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` (200 OK)
   - `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` (200 OK)

---

## Last Updated
2026-09-24 08:22 IST (v1.2.0)
