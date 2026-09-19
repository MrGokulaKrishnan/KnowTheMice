# KNOW THE MICE — Communication & Security Protocol Specification
**Version:** 1.0.0  
**Transport:** UDP Broadcast (Discovery: 52840) + TCP / WebSocket (Control: 52841)  
**Security:** Local-first, Ephemeral X25519 Key Exchange, AES-256-GCM, Monotonic Sequence Replay Protection  

---

## 1. Overview & Principles

KNOW THE MICE is designed around a **Zero-Cloud, Local-First** security philosophy. No mouse movement, keystrokes, pairing codes, or device identities are transmitted outside the local subnet. Communication operates in two phases:
1. **Device Discovery:** Host announces its availability via UDP broadcasts (or responds to multicast queries).
2. **Authenticated Control Session:** Encrypted point-to-point connection over TCP / WebSocket using AES-256-GCM authenticated encryption.

---

## 2. Discovery Protocol

### Broadcast Announcement (Host -> LAN)
- **Port:** UDP `52840`
- **Interval:** Every 2000 ms
- **Broadcast Address:** `255.255.255.255` (and local subnet broadcast)
- **Payload Format (JSON):**
```json
{
  "service": "know-the-mice",
  "version": "1.0.0",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "KRISH-PC",
  "os": "Windows 11 (23H2)",
  "port": 52841,
  "state": "READY"
}
```

### Discovery Query (Android -> LAN)
If discovery broadcasts are dropped by network switches (e.g., IGMP snooping / broadcast suppression), Android sends a discovery probe:
```json
{
  "service": "know-the-mice",
  "type": "DISCOVER",
  "client": "Pixel 8 Pro",
  "clientId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
}
```
The Windows host replies unicast directly to the requesting Android IP.

---

## 3. Cryptographic Handshake & Pairing

### Handshake Sequence
```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Android as Android Client
    participant Windows as Windows Host

    Android->>Windows: Connect TCP/WebSocket (Port 52841)
    Windows-->>Android: HELLO { "host": "KRISH-PC", "protocol": "1.0.0", "salt": "<base64_salt>" }
    
    alt First Time Pairing
        Android->>Windows: PAIR_REQUEST { "clientName": "Pixel 8", "clientId": "uuid", "clientPub": "<base64_x25519>" }
        Windows->>Windows: Generate random 6-digit PIN (e.g. 849201), Start 60s TTL
        Windows-->>User: Display PIN overlay on PC screen
        User->>Android: Enter 6-digit PIN
        Android->>Windows: PAIR_VERIFY { "authProof": "<HMAC-SHA256(sharedSecret, PIN + salt)>", "clientPub": "..." }
        Windows->>Windows: Verify authProof; if OK, store Client ID in DPAPI Trusted Store
        Windows-->>Android: PAIR_SUCCESS { "authToken": "<encrypted_device_token>", "hostPub": "<base64_x25519>" }
    else Returning Trusted Device
        Android->>Windows: AUTH { "clientId": "uuid", "authToken": "<encrypted_token>", "nonce": "...", "clientPub": "..." }
        Windows->>Windows: Validate against DPAPI store
        Windows-->>Android: AUTH_SUCCESS { "sessionKeyDerived": true }
    end

    Note over Android,Windows: Transport now encrypted via AES-256-GCM with sliding sequence window
```

---

## 4. Control Framing & Message Types

### 4.1 High-Frequency Mouse Movement (Binary Framing)
To achieve `<15ms` input latency without JSON serialization overhead, relative mouse movement is packed into a compact 13-byte binary frame:

| Offset | Type | Field | Description |
|---|---|---|---|
| 0 | `uint8` | `Magic` | Always `0x01` (`MSG_MOUSE_MOVE`) |
| 1..4 | `float32` (LE) | `dx` | Horizontal relative delta in screen points |
| 5..8 | `float32` (LE) | `dy` | Vertical relative delta in screen points |
| 9..10 | `uint16` (LE) | `seq` | Monotonically increasing packet sequence counter |
| 11 | `uint8` | `flags` | Bit 0: Left Down, Bit 1: Right Down, Bit 2: Middle Down, Bit 3: Drag Mode |
| 12 | `uint8` | `checksum` | XOR checksum of bytes 0..11 |

### 4.2 Control & Action Messages (JSON Framing)

#### Mouse Click & Scroll
```json
{
  "type": "MOUSE_CLICK",
  "button": "LEFT | RIGHT | MIDDLE | DOUBLE",
  "action": "DOWN | UP | CLICK",
  "seq": 1045
}
```
```json
{
  "type": "MOUSE_SCROLL",
  "dx": 0.0,
  "dy": -3.5,
  "seq": 1046
}
```

#### Keyboard Input
```json
{
  "type": "KEY_INPUT",
  "action": "DOWN | UP | PRESS",
  "key": "A",
  "code": 65,
  "modifiers": ["CTRL", "SHIFT"],
  "seq": 1047
}
```

#### Media Remote
```json
{
  "type": "MEDIA_CMD",
  "action": "PLAY_PAUSE | NEXT | PREV | STOP | VOL_UP | VOL_DOWN | MUTE",
  "seq": 1048
}
```

#### Presentation Mode
```json
{
  "type": "PRESENTATION_CMD",
  "action": "NEXT_SLIDE | PREV_SLIDE | START | END | BLACK_SCREEN | WHITE_SCREEN",
  "seq": 1049
}
```

#### Power Controls (Strictly Gated)
```json
{
  "type": "POWER_CMD",
  "action": "LOCK | SLEEP | HIBERNATE | RESTART | SHUTDOWN",
  "confirmed": true,
  "targetPc": "KRISH-PC",
  "seq": 1050
}
```

#### Application Launcher (Strict Whitelist Only)
```json
{
  "type": "APP_LAUNCH",
  "appId": "Microsoft.WindowsTerminal_8wekyb3d8bbwe!App",
  "seq": 1051
}
```
*Note: Arbitrary command strings (`cmd.exe`, `powershell.exe`, etc.) are unconditionally rejected by the Windows host.*

#### Heartbeat / Ping-Pong
```json
{ "type": "PING", "timestamp": 1726732950123 }
```
```json
{ "type": "PONG", "timestamp": 1726732950123, "hostTime": 1726732950128 }
```

---

## 5. Security & Threat Model

1. **Replay Protection:** Every control message has a monotonic 32-bit sequence counter. Packets with sequence numbers lower than the highest received minus 128 (sliding replay window) are immediately dropped.
2. **Brute Force Rate Limiting:** After 3 incorrect 6-digit PIN attempts, pairing is locked for 5 minutes.
3. **No Credential Exposure:** Private keys never leave the respective devices. No passwords or plaintext secrets are transmitted or logged.
4. **Local Subnet Isolation:** Windows host listens exclusively on local interface bindings (`0.0.0.0:52841` bound with private firewall profile); port forwarding or public relaying is strictly prohibited.
