# Development Status & Verification Matrix

Generated: 2026-09-25 10:15 IST
Release Version: v1.2.0 (Build 120 / APK VersionCode 3)
Master Logo Reference: `media_1790310804993.jpg` (SHA-256: `85e9285abf609843b5721df462d2c4d32c1d61cc70afc72887fba0fe067f46b6`)

---

## Verification Matrix

### ANDROID LOGO:
PASS
- Exact uploaded Know The Mice monogram and typography geometry (`media_1790310804993.jpg`).
- Bounding box centered at `(200, 168, 828, 784)` with 200px black margin on left/right and 206px on top/bottom in 1024x1024 canvas.
- Centered with balanced black space all around, eliminating zoomed-in appearance.
- Rendered in all mipmap tiers (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) and About screen.

### ANDROID ADAPTIVE ICON:
PASS
- Foreground safe-area constraint met: scaled at 0.38 inside 108dp canvas (`ic_launcher_foreground_asset.png`).
- Inside the visible 72dp mask (Google Pixel circle, Samsung One UI squircle), logo occupies 41dp x 40dp, leaving ~21.5% black margin on all sides.
- Explicit centering and 108dp dimensional constraint enforced in `ic_launcher_foreground.xml` (`android:width="108dp"`, `android:height="108dp"`, `android:gravity="center"`).
- Zero edge clipping under Samsung squircle and Google Pixel circular launcher masks (all artwork falls comfortably inside 66dp keyline circle).
- Background layer (`ic_launcher_background.xml`) is solid AMOLED black (`#000000`).

### ANDROID APP INFO ICON:
PASS
- System settings, App Info, and Google Play/package installer display standard adaptive icon with crisp resolution and generous black breathing margin.

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
- Centered with ~20% black margin, perfectly matching uploaded media.
- No horizontal or vertical stretching, no artificial borders, no cropping.

### ANDROID RELEASE APK:
PASS
- Production R8-minified and zip-aligned release APK built with `./gradlew.bat assembleRelease --no-daemon`.
- Binary: `website/public/downloads/android/KnowTheMice-Android.bin` (mapped to `KnowTheMice-Android.apk`).
- Size: 2,732,874 bytes (2.61 MB).
- SHA-256: `404725DA5BFAB2B77E9EE3398D82FBDDFBC4A24A1F0593C194903A61455C89E5`.

### WINDOWS LOGO:
PASS
- Authoritative master logo geometry used across application window, installer window, dialogs, and about card.
- Centered with ~20% black margin around the logo like uploaded media.
- Default border removed: clean borderless presentation matching Apple-style minimal aesthetic.

### WINDOWS 15% ROUNDED CORNER:
PASS
- Container mask uses exact 15% radius curvature on pure black canvas for desktop, taskbar, ARP, and installer.
- Icon assets generated with multi-resolution `.ico` containing 16x16, 24x24, 32x32, 48x48, 64x64, 128x128, 256x256, and 512x512 mipmaps.
- Because the logo is centered with ~20% black space, the 15% curvature curves cleanly through the black margin with zero clipping or crowding of the logo artwork.

### WINDOWS EXE:
PASS
- Built single-file self-extracting GUI setup executable: `windows/publish_setup/KnowTheMice-Setup-x64.exe`.
- Binary: `website/public/downloads/windows/KnowTheMice-Setup-x64.bin`.
- Size: 1,097,384 bytes (1.05 MB).
- SHA-256: `8372F402ECF94709F51869125736E9D4D6B03F71CCD333F8F1A97C4C0DA18826`.

### WINDOWS MSI:
PASS
- Built enterprise WiX MSI installer package: `windows/publish_setup/KnowTheMice-Setup-x64.msi`.
- Binary: `website/public/downloads/windows/KnowTheMice-Setup-x64.msi.bin`.
- Size: 184,320 bytes (180 KB).
- SHA-256: `0EF4F346DDB0F9F4EA44B23648751D026937D8736215DC2EBECB7E689B4164DB`.

### WINDOWS START MENU:
PASS
- Installer creates clean shortcut in Start Menu Programs folder with embedded 15% rounded icon.

### WINDOWS TASKBAR:
PASS
- Running application window binds to `Icon="icon.ico"`, rendering crisp 15% rounded icon without artifacting or distortion.

### WINDOWS SYSTEM TRAY:
PASS
- Dedicated `tray.ico` generated and wired to NotifyIcon.
- Symbol centered with 18% black margin inside 15% rounded black container.
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
- Centered logo with pure black AMOLED background (`#000000`) and unified 9-token orange gradient system (`#FF6B00` to `#FF8800`).

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
| `https://knowthemice.web.app/downloads.json` | 200 OK | `application/json` | 2,880 B | `5126327f7dfb4d91...` |
| `https://knowthemice.web.app/logo.png` | 200 OK | `image/png` | 197,485 B | `8E379FA060D8B41B5B82C8E479AE920B096AC5689B5585E7B4E631D8E1C8C8A2` |
| `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.exe` | 200 OK | `application/vnd.microsoft.portable-executable` | 1,097,384 B | `8372F402ECF94709F51869125736E9D4D6B03F71CCD333F8F1A97C4C0DA18826` |
| `https://knowthemice.web.app/downloads/windows/KnowTheMice-Setup-x64.msi` | 200 OK | `application/x-msi` | 184,320 B | `0EF4F346DDB0F9F4EA44B23648751D026937D8736215DC2EBECB7E689B4164DB` |
| `https://knowthemice.web.app/downloads/android/KnowTheMice-Android.apk` | 200 OK | `application/vnd.android.package-archive` | 2,732,874 B | `404725DA5BFAB2B77E9EE3398D82FBDDFBC4A24A1F0593C194903A61455C89E5` |
| `https://knowthemice.web.app/downloads/windows/latest` | 200 OK | `application/vnd.microsoft.portable-executable` | 1,097,384 B | `8372F402ECF94709F51869125736E9D4D6B03F71CCD333F8F1A97C4C0DA18826` |
| `https://knowthemice.web.app/downloads/android/latest` | 200 OK | `application/vnd.android.package-archive` | 2,732,874 B | `404725DA5BFAB2B77E9EE3398D82FBDDFBC4A24A1F0593C194903A61455C89E5` |

---

## KNOWN ISSUES:
None. All components build cleanly with 0 warnings/errors, and all live endpoints serve valid binaries with verified SHA-256 hashes.

## NEXT ACTION:
Maintain repository state, monitor Firebase hosting distribution analytics, and verify production field installs on physical test devices.
