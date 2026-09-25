# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 16 — Logo Centering Across All Platforms & Live Deployment — COMPLETED

## Current Task
Executed instructions from the latest prompt:
1. **Logo Centering Across All Platforms**:
   - Updated `branding/build-brand-assets.py`: set `y_shift_pct = 0.0` in `place_in_canvas` and exact vertical center `pos_y = (size - new_h) // 2` for tray icon badge.
   - Regenerated all brand assets: Android adaptive foreground assets, mipmaps, drawables, splash logos, Windows application/installer/tray `.ico` files, and Web favicons/PWA/OG icons.
   - Preserved proportions, geometric dimensions, 15% corner radius for Windows, and AMOLED `#000000` background without distortion or cropping.
2. **Production Build & Verification**:
   - Built Windows Single-File Setup EXE (`KnowTheMice-Setup-x64.exe`, 1,097,896 bytes, SHA-256: `9DC246F47FB76A203F4A9AB1D72ECCF9417948C7DDC94A2D72C4DA4AFFEF0831`).
   - Built Windows WiX MSI package (`KnowTheMice-Setup-x64.msi`, 184,320 bytes, SHA-256: `A0705708F69652429819EA58512FE00F7A9ED9C8E6084892EEA512A996BBDB20`).
   - Built Android Release signed APK (`app-release.apk`, 2,749,438 bytes, SHA-256: `88E78D26B3E13766D035D47148131EFFBC83E2DD24500D634548B4F24EC16710`).
   - Updated `downloads.json` and `Download.tsx` with latest checksums and changelog.
   - Rebuilt website with Vite and deployed live to Firebase Hosting (`https://knowthemice.web.app`).
   - Verified all live endpoints with `curl.exe -I` returning HTTP 200 OK.
   - Updated `DEVELOPMENT_STATUS.md` recording PASS for all criteria.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Logo Centering Across All Platforms**:
  - Exact vertical and horizontal centering across all asset pipelines.
  - Regenerated Android adaptive foregrounds, mipmaps, drawables, splash logos, Windows icons (app, installer, tray), and Web icons.
- [x] **CHECKPOINT 02 — Seamless Connection & Background Persistence**:
  - Implemented `ConnectionService.kt` foreground service (`connectedDevice` type).
  - Added permissions in `AndroidManifest.xml`.
  - Added `ACTION_SCREEN_OFF` / `ACTION_SCREEN_ON` receivers.
  - Added `ConnectivityManager.NetworkCallback` for instant reconnect on Wi-Fi return.
  - Implemented exponential backoff engine (1s, 2s, 4s, 8s, 15s, periodic every 20s).
- [x] **CHECKPOINT 03 — Windows Host Reconnect Resilience**:
  - Proactively cleaned up lingering zombie sessions for reconnecting `ClientId`.
  - Added `RELEASE_KEYS` handling calling `InputInjector.ReleaseAllKeys()`.
- [x] **CHECKPOINT 04 — Unified Mouse ↔ Keyboard Remote Screen**:
  - Created `RemoteControlScreen.kt`.
  - Added Apple-style Liquid Glass switcher pill `[ 🖱 Mouse | ⌨ Keyboard ]` in top header bar.
  - Added 1-tap switching in bottom navigation dock.
  - Ensured switching operates 100% in-memory over the same active connection.
  - Automatic modifier key release upon switching from Keyboard to Mouse.
- [x] **CHECKPOINT 05 — Windows Release Packaging**:
  - Built `KnowTheMice-Setup-x64.exe` (1,097,896 bytes, SHA-256: `9DC246F47FB76A203F4A9AB1D72ECCF9417948C7DDC94A2D72C4DA4AFFEF0831`).
  - Built `KnowTheMice-Setup-x64.msi` (184,320 bytes, SHA-256: `A0705708F69652429819EA58512FE00F7A9ED9C8E6084892EEA512A996BBDB20`).
- [x] **CHECKPOINT 06 — Android Release Assembly**:
  - Built `KnowTheMice-Android.apk` (2,749,438 bytes, SHA-256: `88E78D26B3E13766D035D47148131EFFBC83E2DD24500D634548B4F24EC16710`).
- [x] **CHECKPOINT 07 — Production Deployment & Live Verification**:
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Verified live via `curl.exe -I` on all endpoints (`downloads.json`, `.exe`, `.msi`, `.apk`, `latest` aliases).
- [x] **CHECKPOINT 08 — Verification Documentation**:
  - Updated `DEVELOPMENT_STATUS.md` with all checks marked PASS.

---

## Last Verified State
- `https://knowthemice.web.app/` -> 200 OK (6,215 bytes)
- `https://knowthemice.web.app/downloads.json` -> 200 OK (2,932 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` -> 200 OK (1,097,896 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` -> 200 OK (184,320 bytes)
- `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` -> 200 OK (2,749,438 bytes)
- `https://knowthemice.web.app/downloads/windows/latest` -> 200 OK (1,097,896 bytes)
- `https://knowthemice.web.app/downloads/android/latest` -> 200 OK (2,749,438 bytes)
- `https://knowthemice.web.app/logo.png` -> 200 OK (197,485 bytes)

---

## Last Updated
2026-09-25 13:58 IST (v1.2.0)


