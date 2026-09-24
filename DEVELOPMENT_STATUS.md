# Development Status & Verification Matrix

Generated: 2026-09-24 19:25 IST
Release Version: v1.2.0 (Build 120 / APK VersionCode 3)
Master Logo Reference: `media_1790256187709.jpg` (SHA-256: `86eaaf677a7b4ebd06dcf5164e7b169cb31c5d54854fb4ae32554e8512b06454`)

---

## Verification Matrix

### ANDROID LOGO:
PASS
- Exact uploaded Know The Mice monogram and typography geometry (`media_1790256187709.jpg`).
- Bounding box accurately segmented at `(166, 167, 861, 828)` with symbol ending at `y=685`, ensuring zero distortion or lightning bolt clipping.
- Rendered in all mipmap tiers (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) and About screen.

### ANDROID ADAPTIVE ICON:
PASS
- Foreground safe-area constraint met: centered at 60% within 108dp canvas (`ic_launcher_foreground.png`).
- Zero edge clipping under squircle, circle, rounded rectangle, or teardrop launcher masks.
- Background layer (`ic_launcher_background.xml`) is solid AMOLED black (`#000000`).

### ANDROID APP INFO ICON:
PASS
- System settings, App Info, and Google Play/package installer display standard adaptive icon with crisp resolution.

### ANDROID SPLASH:
PASS
- Pure AMOLED `#000000` background.
- Native Compose `SplashScreen.kt` integrated into `MainActivity.kt` with start destination `"splash"`.
- Background initialization deferred: `startDiscovery()` and `checkForUpdates()` run asynchronously in background coroutines after splash completes, eliminating main-thread stalls.

### ANDROID ANIMATION:
PASS
- 60–120 FPS hardware-accelerated rendering using `graphicsLayer` (alpha, scale, rotation).
- Radial energy glow pulse matching orange brand theme (`#FF6B00` to `#FF8800`).
- System `ANIMATOR_DURATION_SCALE == 0f` reduced-motion accessibility respected (fast-forwards to settled logo and transitions to Home).

### ANDROID LOGO SCALING:
PASS
- Preserves exact aspect ratio of the master logo.
- No horizontal or vertical stretching, no artificial borders, no cropping.

### ANDROID RELEASE APK:
PASS
- Production R8-minified and zip-aligned release APK built with `./gradlew.bat assembleRelease --no-daemon`.
- Binary: `website/public/downloads/android/KnowTheMice-Android.bin` (mapped to `KnowTheMice-Android.apk`).
- Size: 3,018,638 bytes (2.88 MB).
- SHA-256: `C64494F25558DB9C78E5A9BC2D0A3034D01264F51786BA3EC27A4F0E83B848E6`.

### WINDOWS LOGO:
PASS
- Authoritative master logo geometry used across application window, installer window, dialogs, and about card.
- Default border removed: clean borderless presentation matching Apple-style minimal aesthetic.

### WINDOWS 15% ROUNDED CORNER:
PASS
- Container mask uses exact 15% radius curvature on pure black canvas for desktop, taskbar, ARP, and installer.
- Icon assets generated with multi-resolution `.ico` containing 16x16, 24x24, 32x32, 48x48, 64x64, 128x128, 256x256, and 512x512 mipmaps.

### WINDOWS EXE:
PASS
- Built single-file self-extracting GUI setup executable: `windows/publish_setup/KnowTheMice-Setup-x64.exe`.
- Binary: `website/public/downloads/windows/KnowTheMice-Setup-x64.bin`.
- Size: 1,442,472 bytes (1.38 MB).
- SHA-256: `6BB338D568A5713C661FC9861001A7BD944F8AC03D79E0B656CF740E52207E3E`.

### WINDOWS MSI:
PASS
- Built enterprise WiX MSI installer package: `windows/publish_setup/KnowTheMice-Setup-x64.msi`.
- Binary: `website/public/downloads/windows/KnowTheMice-Setup-x64.msi.bin`.
- Size: 225,280 bytes (220 KB).
- SHA-256: `52972508B1E61698C127D1CB22BAF7D027943BDAECD8359604F0F5C58E478689`.

### WINDOWS START MENU:
PASS
- Installer creates clean shortcut in Start Menu Programs folder with embedded 15% rounded icon.

### WINDOWS TASKBAR:
PASS
- Running application window binds to `Icon="icon.ico"`, rendering crisp 15% rounded icon without artifacting or distortion.

### WINDOWS SYSTEM TRAY:
PASS
- Dedicated `tray.ico` generated and wired to NotifyIcon.
- Left-click toggles Show/Hide; right-click opens native context menu with Status, Settings, and Exit.

### WEBSITE LOADING LOGO:
PASS
- HTML inline preloader in `website/index.html` displays master logo (`/logo.png`) with animated pulse ring.
- React Suspense fallback mirrors same visual branding.

### WEBSITE FAVICON:
PASS
- Favicons regenerated from master logo: `favicon.ico`, `favicon.svg`, `apple-touch-icon.png` (180x180), and PWA icons (192x192, 512x512).

### WEBSITE BRANDING:
PASS
- Navigation bar, footer, hero graphic, download cards, and About & Contact page reference master logo.
- Pure black AMOLED background (`#000000`) and unified 9-token orange gradient system (`#FF6B00` to `#FF8800`).

### REGRESSION TEST:
PASS
- Android OTA update system tested with multi-hop redirect handling in `UpdateManager.kt`.
- Android navigation bar touch target collision fixed: decoupled Header and Dock, added prominent center Mouse elevation.
- Wi-Fi discovery (`DiscoveryService.kt` and `DiscoveryServer.cs`) functional and isolated from UI threads.
- All live production endpoints deployed to Firebase Hosting return HTTP 200 OK with correct MIME types and Content-Disposition attachments.

---

## Production Verification Endpoints (Live)

| Endpoint | Status | Content-Type | Size | Checksum (SHA-256) |
|---|---|---|---|---|
| `https://knowthemice.web.app/` | 200 OK | `text/html; charset=utf-8` | 6,215 B | N/A |
| `https://knowthemice.web.app/downloads.json` | 200 OK | `application/json` | 2,880 B | `c2ef4c1d06359fdd...` |
| `https://knowthemice.web.app/logo.png` | 200 OK | `image/png` | 298,319 B | `B278011C30AC13EE62328CC9F1D6BF82B7211BB54A7E818B148A400DCA9CDD7F` |
| `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` | 200 OK | `application/vnd.microsoft.portable-executable` | 1,442,472 B | `6BB338D568A5713C661FC9861001A7BD944F8AC03D79E0B656CF740E52207E3E` |
| `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` | 200 OK | `application/x-msi` | 225,280 B | `52972508B1E61698C127D1CB22BAF7D027943BDAECD8359604F0F5C58E478689` |
| `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` | 200 OK | `application/vnd.android.package-archive` | 3,018,638 B | `C64494F25558DB9C78E5A9BC2D0A3034D01264F51786BA3EC27A4F0E83B848E6` |
| `https://knowthemice.web.app/downloads/windows/latest` | 200 OK | `application/vnd.microsoft.portable-executable` | 1,442,472 B | `6BB338D568A5713C661FC9861001A7BD944F8AC03D79E0B656CF740E52207E3E` |
| `https://knowthemice.web.app/downloads/android/latest` | 200 OK | `application/vnd.android.package-archive` | 3,018,638 B | `C64494F25558DB9C78E5A9BC2D0A3034D01264F51786BA3EC27A4F0E83B848E6` |

---

## KNOWN ISSUES:
None. All components build cleanly with 0 warnings/errors, and all live endpoints serve valid binaries with verified SHA-256 hashes.

## NEXT ACTION:
Maintain repository state, monitor Firebase hosting distribution analytics, and verify production field installs on physical test devices.
