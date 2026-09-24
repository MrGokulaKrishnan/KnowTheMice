# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 12 — Android Adaptive Icon Safe-Zone Calibration (Samsung & Pixel Launcher Masks) & Live Production Deployment — COMPLETED

## Current Task
Executed all instructions from the latest prompt:
1. Resolved Android APK icon clipping / zoom-in under Samsung One UI (squircle mask) and Google Pixel Launcher (circular mask).
2. Recalibrated adaptive icon foreground asset scale to 0.42, keeping the entire rectangular logo comfortably within the 66dp Android keyline safe circle (0 pixels clipped under 72dp Pixel circle and Samsung squircle).
3. Added explicit 108dp width/height constraint and `gravity="center"` in `ic_launcher_foreground.xml`.
4. Adjusted legacy round icon scale to 0.62 (0 pixels clipped by circular mask) and square icon scale to 0.72.
5. Recompiled Android production release APK with R8 shrinking and signing.
6. Updated `downloads.json` and `Download.tsx` with new checksum and size.
7. Deployed to Firebase Hosting and verified live with `curl.exe -I`.
8. Updated `DEVELOPMENT_STATUS.md` recording PASS for all verification criteria.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Master Artwork Updated**:
  - `shared/logo.jpg` updated with `media_1790256187709.jpg`.
  - Regenerated all cross-platform assets with `branding/build-brand-assets.py`: master PNG/SVG, web favicons/PWA/OG, Android adaptive drawables/mipmaps, Windows `.ico` multi-resolution files (16px, 24px, 32px, 48px, 64px, 128px, 256px, 512px), and Linux icons.
- [x] **CHECKPOINT 02 — Android Splash Screen & Startup Performance**:
  - Created `SplashScreen.kt` with GPU-accelerated graphics layer, radial flame glow, smooth easing, and `isReducedMotion` support via system `ANIMATOR_DURATION_SCALE`.
  - Updated `MainActivity.kt`: starts on `"splash"`, defers `discoveryClient.startDiscovery()` and `UpdateManager.checkForUpdates()` to background coroutines triggered after splash ends.
  - Updated `SettingsScreen.kt`: displays master logo in the About card with build info.
- [x] **CHECKPOINT 03 — Windows 15% Rounded Corner & Multi-res Icons**:
  - Borderless 15% corner radius icon for Windows desktop, taskbar, ARP, system tray, and installer.
  - Setup executable embeds 15% rounded multi-res icon.
- [x] **CHECKPOINT 04 — Dual Windows Packaging**:
  - Built `KnowTheMice-Setup-x64.exe` (1,442,472 bytes, SHA-256: `6BB338D568A5713C661FC9861001A7BD944F8AC03D79E0B656CF740E52207E3E`).
  - Built `KnowTheMice-Setup-x64.msi` (225,280 bytes, SHA-256: `52972508B1E61698C127D1CB22BAF7D027943BDAECD8359604F0F5C58E478689`).
- [x] **CHECKPOINT 05 — Android Icon Safe Zone Calibration**:
  - Fixed zoom-in and corner clipping on Samsung One UI and Google Pixel launchers.
  - Scaled foreground asset to 0.42 (108dp canvas), placing all artwork well within the 66dp keyline circle.
  - Added explicit centering and `width="108dp"`, `height="108dp"` to `ic_launcher_foreground.xml`.
  - Scaled legacy round icon to 0.62 with zero pixel clipping.
- [x] **CHECKPOINT 06 — Android Release Assembly**:
  - Built `KnowTheMice-Android.apk` (2,912,990 bytes, SHA-256: `34145961CF210F14E704CA2BB4215647A94CFCE900F95CDBD7754C43FB9CFF5A`).
- [x] **CHECKPOINT 07 — Production Deployment & Live Verification**:
  - Updated `downloads.json` and `Download.tsx` with exact binary sizes and checksums.
  - Built web distribution with Vite (`npm run build`).
  - Synced downloads to `website/dist/downloads/`.
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Verified live via `curl.exe -I` on all endpoints (`downloads.json`, `.exe`, `.msi`, `.apk`, `logo.png`, `latest` aliases).
- [x] **CHECKPOINT 08 — Verification Documentation**:
  - Updated `DEVELOPMENT_STATUS.md` with all checks marked PASS.

---

## Last Verified State
- `https://knowthemice.web.app/` -> 200 OK (6,215 bytes)
- `https://knowthemice.web.app/downloads.json` -> 200 OK (2,880 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` -> 200 OK (1,442,472 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` -> 200 OK (225,280 bytes)
- `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` -> 200 OK (2,912,990 bytes)
- `https://knowthemice.web.app/logo.png` -> 200 OK (298,319 bytes)
- `https://knowthemice.web.app/downloads/windows/latest` -> 200 OK (1,442,472 bytes)
- `https://knowthemice.web.app/downloads/android/latest` -> 200 OK (2,912,990 bytes)

---

## Last Updated
2026-09-24 22:50 IST (v1.2.0)
