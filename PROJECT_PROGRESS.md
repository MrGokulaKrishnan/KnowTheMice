# Know The Mice — Project Progress & State Resume

## Current Phase
PHASE 15 — Optical Upward Adjustment of Brand Emblem Across Platforms & Live Deployment — COMPLETED

## Current Task
Executed all instructions from the latest prompt:
1. **Optical Centering & Upward Shift for Brand Emblem**:
   - Analyzed optical center vs bounding-box mathematical center: the heavy visual weight of the top KM monogram vs bottom subtitle text caused the emblem to appear weighted downwards in circular/squircle containers (Android launcher masks on Samsung One UI and Google Pixel, Windows desktop/tray, website favicons, splash screen).
   - Updated `branding/build-brand-assets.py`: introduced optical elevation offset `y_shift_pct = -0.045` (-4.5% vertical offset) for all square/round containers and `-0.03` for tray icon badge.
   - Regenerated all brand assets: Android adaptive foreground assets, mipmaps, drawables, splash logos, Windows application/installer/tray `.ico` files, and Web favicons/PWA/OG icons.
   - Proportions, aspect ratio, geometric dimensions, 15% corner radius for Windows, and AMOLED `#000000` background preserved without distortion or cropping.
2. **Background / Screen-Off Connection Persistence**:
   - Implemented `ConnectionService.kt`: Android Foreground Service (`connectedDevice` foreground service type) with persistent notification (`KNOWTHEMICE: Connected to [PC]`), ensuring Android Doze/App Standby does not kill the TCP socket.
   - Declared permissions `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_CONNECTED_DEVICE`, and `POST_NOTIFICATIONS` in `AndroidManifest.xml`.
3. **Exponential Backoff Auto-Reconnect Engine**:
   - Implemented `ConnectionManager.kt`: Persistent connection manager outliving Activity lifecycle.
   - Backoff retry engine with retry intervals: 1s, 2s, 4s, 8s, 15s, then periodic background retries every 20s.
   - Screen ON/OFF listeners: `ACTION_SCREEN_OFF` sets state to `BACKGROUND_CONNECTED` and pauses sensors; `ACTION_SCREEN_ON` tests socket liveness immediately and triggers instant zero-delay reconnect if dropped.
   - Network callback: `ConnectivityManager.NetworkCallback` triggers immediate reconnect when Wi-Fi re-establishes.
   - Heartbeat timeout and dead connection detection (>8s without pong from host triggers reconnect).
4. **Windows Host Zombie Session Cleanup**:
   - Updated `NetworkServer.cs`: When a client connects or re-authenticates with matching `ClientId`, lingering dead sessions are proactively closed and removed from `_activeSessions`.
   - Added support for `"RELEASE_KEYS"` command in `NetworkServer.cs`, triggering `InputInjector.ReleaseAllKeys()` to clean up sticky modifiers.
5. **Unified Mouse ↔ Keyboard UX (1-Tap Switching)**:
   - Implemented `RemoteControlScreen.kt`: Merged Mouse and Keyboard into a unified screen operating over the single active socket session. Zero disconnects, zero re-authentication, zero discovery, zero re-pairing.
   - Top Header Switcher: Apple-style Liquid Glass pill toggle `[ 🖱 Mouse | ⌨ Keyboard ]` for instant 1-tap mode switching.
   - Bottom Dock: Dedicated "Mouse" and "Keyboard" tabs reflecting active mode with 1-tap switching alongside "Media" and "Slides".
   - Sticky modifier safety release: Switching back from Keyboard to Mouse automatically calls `releaseAllKeys()` (clearing `Ctrl`, `Alt`, `Shift`, `Win` on PC) and resets UI modifier states to prevent stuck keys on Windows.
6. **Production Build & Verification**:
   - Built Windows Single-File Setup EXE (`KnowTheMice-Setup-x64.exe`, 1,097,384 bytes, SHA-256: `8D511C2D710F9E62F6E201D21F4F04C6404E1A524C200629F0CC806FF209DAD9`).
   - Built Windows WiX MSI package (`KnowTheMice-Setup-x64.msi`, 188,416 bytes, SHA-256: `CE68B043DE17BABFB6CEE71BECFAA934B25E1984E84355A95A1CD015D8C4A7A0`).
   - Built Android Release signed APK (`app-release.apk`, 2,749,270 bytes, SHA-256: `8B7E114E409795B57A2E8AD43937466A47CC83756608D7F96D402FCDD28EBBF2`).
   - Updated `downloads.json` and `Download.tsx` with latest checksums and changelog.
   - Rebuilt website with Vite and deployed live to Firebase Hosting (`https://knowthemice.web.app`).
   - Verified all live endpoints with `curl.exe -I` returning HTTP 200 OK.
   - Updated `DEVELOPMENT_STATUS.md` recording PASS for all criteria.

## Overall Status
COMPLETED & VERIFIED

---

## Checkpoint Status

- [x] **CHECKPOINT 01 — Optical Upward Adjustment of Brand Emblem**:
  - Elevated brand emblem by -4.5% across all asset pipelines.
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
  - Built `KnowTheMice-Setup-x64.exe` (1,097,384 bytes, SHA-256: `8D511C2D710F9E62F6E201D21F4F04C6404E1A524C200629F0CC806FF209DAD9`).
  - Built `KnowTheMice-Setup-x64.msi` (188,416 bytes, SHA-256: `CE68B043DE17BABFB6CEE71BECFAA934B25E1984E84355A95A1CD015D8C4A7A0`).
- [x] **CHECKPOINT 06 — Android Release Assembly**:
  - Built `KnowTheMice-Android.apk` (2,749,270 bytes, SHA-256: `8B7E114E409795B57A2E8AD43937466A47CC83756608D7F96D402FCDD28EBBF2`).
- [x] **CHECKPOINT 07 — Production Deployment & Live Verification**:
  - Deployed to Firebase Hosting (`https://knowthemice.web.app`).
  - Verified live via `curl.exe -I` on all endpoints (`downloads.json`, `.exe`, `.msi`, `.apk`, `latest` aliases).
- [x] **CHECKPOINT 08 — Verification Documentation**:
  - Updated `DEVELOPMENT_STATUS.md` with all checks marked PASS.

---

## Last Verified State
- `https://knowthemice.web.app/` -> 200 OK (6,215 bytes)
- `https://knowthemice.web.app/downloads.json` -> 200 OK (2,932 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` -> 200 OK (1,097,384 bytes)
- `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` -> 200 OK (188,416 bytes)
- `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` -> 200 OK (2,749,270 bytes)
- `https://knowthemice.web.app/downloads/windows/latest` -> 200 OK (1,097,384 bytes)
- `https://knowthemice.web.app/downloads/android/latest` -> 200 OK (2,749,270 bytes)
- `https://knowthemice.web.app/logo.png` -> 200 OK (197,310 bytes)

---

## Last Updated
2026-09-25 13:48 IST (v1.2.0)

