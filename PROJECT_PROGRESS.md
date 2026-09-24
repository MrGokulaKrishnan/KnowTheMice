# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 7 — Logo Single Source of Truth, Update State Machine & Packaging (Directives 31–49) — COMPLETED

## Current Task
All tasks and deliverables for Phase 7 completed, verified live, and staged for release.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Project Audited & Unified Design System**: 9 canonical gradient tokens implemented in `shared/theme.json`, web Tailwind/CSS, Android `Color.kt`, and Windows `App.xaml`.
- [x] **CHECKPOINT 02 — Authoritative Master Logo Established**: Authoritative logo (`shared/logo.jpg`, derived from user-uploaded `media_1790183489684.jpg`) with KM monogram, lightning bolts, and orange cursor arrow.
- [x] **CHECKPOINT 03 — Android 15% Corner Radius & Adaptive Icon**:
  - `make_rounded_icon(legacy_square, 0.15)` applied to `mipmap-*/ic_launcher.png` (corner alpha = 0, inner alpha = 255).
  - Adaptive icon foreground centered at 60% safe zone inside 108dp canvas on AMOLED black background (`ic_launcher_background.xml`).
  - Master APK 512x512 icon generated at `shared/apk_icon.png` and `branding/android/ic_launcher_15pct.png`.
- [x] **CHECKPOINT 04 — Windows Host Process & Tray Stability**:
  - `ShutdownMode="OnExplicitShutdown"` set in `App.xaml`.
  - Minimize-to-tray on window close in `MainWindow.xaml.cs`.
  - High-contrast AMOLED squircle tray badge with flame orange border (`16, 20, 24, 32, 48, 64px`) in `tray.ico`.
- [x] **CHECKPOINT 05 — Windows Update State Machine**:
  - Refactored `UpdateService.cs` with explicit `UpdateState` (`Idle`, `Checking`, `UpToDate`, `UpdateAvailable`, `Downloading`, `Ready`, `Failed`).
  - Persistent state in `%LocalAppData%\KnowTheMice\update_staged.json`.
  - "Restart to Update" is **strictly hidden** when up to date, checking, or downloading. It appears **only** in `Ready` (`RestartRequired`) state.
- [x] **CHECKPOINT 06 — Android Native System Keyboard IME Rebuild**:
  - Replaced custom drawn QWERTY buttons with native Android system IME (Gboard, Samsung Keyboard, SwiftKey).
  - Selectable input modes (Text, Multiline, Number, URL, Email, Password).
  - `Modifier.imePadding()` with voice dictation support and compact PC modifier toolbar.
- [x] **CHECKPOINT 07 — Android Update State Machine**:
  - `UpdateManager.kt` refactored with `AndroidUpdateState` sealed class and persistent storage in `context.getSharedPreferences("knowthemice_update")`.
  - `HomeScreen.kt` OTA banner dynamically renders based on state; "Restart to Update" is visible only when APK is downloaded and verified.
- [x] **CHECKPOINT 08 — Windows Setup Single-File EXE**:
  - `KnowTheMice-Setup-x64.exe` built with embedded multi-resolution `icon.ico` (1,511,592 bytes, SHA-256: `49E6C14B51C6195881EDF7D3A55A1FF74352D25FB2EB41738F47B9A0200598BD`).
- [x] **CHECKPOINT 09 — Windows WiX MSI Installer Build**:
  - `KnowTheMice-Setup-x64.msi` built with embedded icon, ARP properties, per-machine install, and Start Menu/Desktop shortcuts (229,376 bytes, SHA-256: `971F51E1C884EE6A9142F1717DAF6C09EF6182D8B0FE2C5566609B9A46CD8945`).
- [x] **CHECKPOINT 10 — Android Release APK Build**:
  - Built signed release APK (`KnowTheMice-Android.apk`, 2,609,198 bytes, SHA-256: `79674423793820E9C8A64086627E9118E3AF7811BE0C4B347A2597F31840CE49`).
- [x] **CHECKPOINT 11 — Production Deployment & Live Verification**:
  - Updated `downloads.json` and `Download.tsx` with hashes.
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Verified endpoints with `curl -I`:
    - `/downloads.json` -> 200 OK (2,911 bytes)
    - `/downloads/windows/KnowTheMice-Setup-x64.exe` -> 200 OK (1,511,592 bytes)
    - `/downloads/windows/KnowTheMice-Setup-x64.msi` -> 200 OK (229,376 bytes)
    - `/downloads/android/KnowTheMice-Android.apk` -> 200 OK (2,609,198 bytes)

---

## Last Verified State
1. `branding/build-brand-assets.py` successfully executed:
   - Generated all multi-resolution Windows icons (`icon.ico`, `tray.ico`), Android adaptive drawables and 15% radius mipmaps, Linux SVG/PNGs, and Web favicons.
2. Android Gradle build (`.\gradlew.bat assembleRelease`) completed with **BUILD SUCCESSFUL**.
3. Windows Host and Setup builds completed with **Build succeeded (0 errors)**.
4. Single-file installer EXE (`KnowTheMice-Setup-x64.exe`) and WiX MSI (`KnowTheMice-Setup-x64.msi`) built.
5. Production website built and deployed to Firebase Hosting with live HTTP 200 verification on all binary endpoints.

---

## Important Architectural Decisions
1. **Single Source of Truth**: All platform assets strictly derived from `shared/logo.jpg` using PIL pipeline; no ad-hoc icon creation.
2. **15% Radius Container**: Applied at the icon container level for Android launcher mipmaps while keeping the internal KM logo geometry and aspect ratio 100% unaltered.
3. **Adaptive Icon 60% Safe Zone**: Foreground artwork scaled to 60% of 108dp canvas, ensuring 0% clipping on circular, squircle, and round-rect OEM masks.
4. **State Machine Separation**: "Restart to Update" cannot be triggered or shown unless an installer is staged and SHA-256 verified on disk. Normal restart is decoupled from update restart.
5. **Dual Windows Distribution**: Offering both single-file self-extracting EXE and standard enterprise WiX MSI installer with embedded branding.
6. **Firebase Hosting Binary Rewrites**: Stored as `.bin` internally to satisfy Firebase Spark constraints while serving with direct `.exe`, `.msi`, and `.apk` URLs, MIME types, and `Content-Disposition: attachment`.

---

## Last Updated
2026-09-24 07:45 IST (v1.2.0)
