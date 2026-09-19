# KNOW THE MICE — Troubleshooting & Diagnostics Guide

## Common Questions & Resolutions

### 1. "PC Not Found" on Android
- **AP / Client Isolation:** In hotel, campus, or router guest networks, "Client Isolation" stops Wi-Fi clients from communicating. Switch to your main home Wi-Fi network.
- **Subnet Mismatch:** Verify your PC and phone share the same IP prefix (e.g. `192.168.1.xxx`).
- **Manual Connection:** Tap "Connect Manually" on Android and enter your PC's IP address shown on the Windows dashboard.

### 2. Windows Firewall Blocks Incoming Connections
Run PowerShell as Administrator and execute:
```powershell
netsh advfirewall firewall add rule name="Know The Mice Host" dir=in action=allow protocol=TCP localport=52841 profile=private
netsh advfirewall firewall add rule name="Know The Mice Discovery" dir=in action=allow protocol=UDP localport=52840 profile=private
```

### 3. Jittery or Lagging Cursor Movement
- Switch from 2.4GHz Wi-Fi to a 5GHz Wi-Fi network.
- In Android Settings -> Remote Settings, adjust the Sensitivity slider and enable Pointer Smoothing.
- Avoid saturating the local Wi-Fi with heavy torrenting or high-bitrate streaming during real-time remote control.
