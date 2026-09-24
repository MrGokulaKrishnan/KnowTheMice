# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 9 — Official Logo Update (Media 1790234295905), 15% Windows-Only Corner Radius & Live Deployment — COMPLETED

## Current Task
Executed all instructions from the latest prompt:
1. Updated authoritative master logo across Android and Windows from `media_1790234295905.jpg`.
2. Android APK file logo set SAME AS UPLOADED IN MEDIA (no artificial corner cutouts).
3. Windows `.exe`, `.msi`, and `.ico` files generated with 15% corner radius container on AMOLED black with zero border.
4. Inside Windows applications (`MainWindow.xaml`, `InstallerWindow.xaml`), logo containers set to 15% corner radius.
5. Inside Android application (`HomeScreen.kt`), logo container set to square matching uploaded media.
6. Recompiled Windows Host, single-file Setup EXE, and enterprise WiX MSI installer.
7. Recompiled Android release APK with R8 minification and signing.
8. Deployed to Firebase Hosting and verified live with `curl.exe -I`.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Master Artwork Updated**:
  - `shared/logo.jpg` updated with `media_1790234295905.jpg` (SHA256: `85e9285abf609843b5721df462d2c4d32c1d61cc70afc72887fba0fe067f46b6`).
  - Extracted full artwork (y: 168 to 783, x: 200 to 827) and isolated symbol (y: 168 to 645).
- [x] **CHECKPOINT 02 — Android APK Logo Same as Uploaded Media**:
  - Launcher mipmaps (`ic_launcher.png`) and 512x512 APK icon (`shared/apk_icon.png`) generated square without artificial radius cutouts.
  - Adaptive icon safe-zone centering at 60% scale within 108dp canvas (`ic_launcher_background.xml`).
  - Android in-app header logo in `HomeScreen.kt` rendered square matching media.
- [x] **CHECKPOINT 03 — Windows 15% Radius in Corner (EXE, MSI, and Inside App)**:
  - `icon.ico`, `app.ico`, `installer.ico`, and `tray.ico` generated with 15% corner radius container on AMOLED black with no border.
  - `MainWindow.xaml`: Logo container set to 15% corner radius (`CornerRadius="6"` on 38px).
  - `InstallerWindow.xaml`: Logo container set to 15% corner radius (`CornerRadius="14"` on 96px).
- [x] **CHECKPOINT 04 — Dual Windows Packaging**:
  - Built `KnowTheMice-Setup-x64.exe` (1,496,744 bytes, SHA-256: `D5AD3241A52B489F5B1AD72F9F6B7F28F64ACBA13FC26F5B7E74E6391E1C8E3A`).
  - Built `KnowTheMice-Setup-x64.msi` (233,472 bytes, SHA-256: `73ABD3ABA2F595B615BF54E196A537F78B26752AF1520EE21DEC2A2F872E9FE3`).
- [x] **CHECKPOINT 05 — Android Release Assembly**:
  - Built `KnowTheMice-Android.apk` (2,744,778 bytes, SHA-256: `86F08DB3D157FCF6DAED10CAB5C312E74335F0465B77B42F61E4BEF6C3A728F8`).
- [x] **CHECKPOINT 06 — Production Deployment & Live Verification**:
  - Updated `downloads.json` and `Download.tsx`.
  - Built web distribution with Vite (`npm run build`).
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Verified live via `curl.exe -I` on all endpoints (`downloads.json`, `.exe`, `.msi`, `.apk`).

---

## Last Verified State
- `https://knowthemice.web.app/downloads.json` -> 200 OK (2,880 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` -> 200 OK (1,496,744 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` -> 200 OK (233,472 bytes)
- `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` -> 200 OK (2,744,778 bytes)

---

## Last Updated
2026-09-24 13:02 IST (v1.2.0)
