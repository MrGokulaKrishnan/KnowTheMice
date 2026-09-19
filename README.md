# KNOW THE MICE
> **Your phone. Your computer. One powerful remote.**

[![Protocol](https://img.shields.io/badge/protocol-v1.0.0-orange.svg)](protocol/protocol.md)
[![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Android%20%7C%20Web-blue.svg)](#)
[![Security](https://img.shields.io/badge/crypto-X25519%20%2B%20AES--256--GCM-green.svg)](docs/security.md)
[![License](https://img.shields.io/badge/license-MIT-purple.svg)](#)

**Know The Mice** is a production-grade, zero-cloud remote-control ecosystem that turns an Android smartphone into an ultra-low-latency wireless mouse, touchpad, full keyboard, gyroscope air mouse, media deck, and presentation controller for Windows PCs over local Wi-Fi.

---

## Deliverables & Binary Artifacts

1. **Website (Built & Deployable):**
   - Source: `website/` (React + Vite + TypeScript + Tailwind CSS + Framer Motion)
   - Production Build Output: `website/dist/` (`npm run build`)
2. **Windows Desktop Application:**
   - Standalone Executable: `windows/KnowTheMice.Host/bin/Release/net8.0-windows/win-x64/publish/KnowTheMice.Host.exe`
   - Installable MSI Package: `windows/KnowTheMice.Installer/KnowTheMice-Setup-v1.0.0-win-x64.msi`
3. **Android Application:**
   - Source: `android/` (Kotlin 1.9 + Jetpack Compose + Coroutines)
   - Keystore: `android/debug.keystore`

---

## Core Features

- **Fluid Multi-Touch Touchpad:** Two-finger natural vertical/horizontal scrolling, tap-to-click, two-finger right click, drag-and-drop hold, and tactile haptic vibration.
- **6-DOF Gyroscope Air Mouse:** Sensor fusion combining gyroscope and accelerometer with dead-zone filtering. Point your phone like a laser wand to control your PC from the couch.
- **Full Remote Virtual Keyboard:** QWERTY keyboard with Ctrl, Alt, Shift, Windows key, Esc, Tab, F1-F12, navigation arrows, and voice dictation.
- **Presentation Remote Deck:** Large Next/Prev slide buttons, presenter stopwatch timer, black screen toggle ('B'), and vibration alerts.
- **Universal Media Controller:** Native media scan codes for Spotify, YouTube, Netflix, and VLC. Master volume adjustment and mute.
- **Whitelisted Application Launcher:** Browse and launch installed Windows applications safely without exposing command shells or terminal injection risks.
- **Confirmation-Gated Power Controls:** Lock, Sleep, Hibernate, Restart, and Shutdown strictly protected by two-step confirmation matching the target PC name.

---

## Visual Design Language: Liquid Glass + Orange Gradient

Inspired by the brand's geometric monogram logo, the interface uses a dark AMOLED (`#050505`) base with glossy orange gradient highlights (`#FF8A00` → `#FF5A00` → `#D93600`), translucent liquid-glass surfaces (`backdrop-filter: blur(24px)`), subtle orange glows, and high-contrast typography (Orbitron + Inter).

---

## Security Architecture

- **No-Cloud Principle:** 100% offline-first local network operation. No mandatory cloud accounts, no subscriptions, no telemetry tracking.
- **Cryptographic Pairing:** 6-digit one-time PIN with 60-second TTL and 3-strike brute-force lockout.
- **Transport Encryption:** Ephemeral X25519 key exchange + AES-256-GCM authenticated encryption.
- **Anti-Replay Protection:** 32-bit monotonic sequence counter with a 128-packet sliding replay window.
- **Zero Shell Execution:** `cmd.exe` and PowerShell execution are strictly prohibited on the Windows host.
- **Credential Storage:** Windows DPAPI (Data Protection API) and Android Keystore.

---

## Build Instructions

### 1. Website
```bash
cd website
npm install
npm run build
# Built files are generated in website/dist/
```

### 2. Windows Host & MSI Installer
```powershell
cd windows
# Build and compile solution
dotnet build -c Release

# Publish self-contained executable
dotnet publish KnowTheMice.Host/KnowTheMice.Host.csproj -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true

# Build installable MSI
cd KnowTheMice.Installer
wix build Package.wxs -o KnowTheMice-Setup-v1.0.0-win-x64.msi
```

### 3. Android Application
```bash
cd android
./gradlew assembleDebug
# Debug APK generated at app/build/outputs/apk/debug/app-debug.apk

./gradlew assembleRelease
# Release APK signed with debug.keystore generated at app/build/outputs/apk/release/app-release.apk
```

---

## License
MIT License. Developed for the Know The Mice ecosystem.
