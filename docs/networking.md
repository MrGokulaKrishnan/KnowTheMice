# KNOW THE MICE — Networking Architecture

## 1. Port Assignments & Protocols

| Port | Transport | Purpose | Payload Framing |
|---|---|---|---|
| `52840` | UDP Broadcast | Local Host Discovery & mDNS Probing | JSON Beacon Announcement |
| `52841` | TCP Stream | Encrypted Control Channel & Input Stream | 13-Byte Binary Motion / JSON Control |

---

## 2. Low-Latency Optimization Strategies

1. **TCP_NODELAY (Nagle's Algorithm Disabled):**  
   Both Android client and Windows server enable `TCP_NODELAY` on active sockets to guarantee that mouse movement frames are transmitted immediately without being buffered by the TCP stack.

2. **Compact 13-Byte Binary Motion Framing:**  
   Mouse coordinates are serialized as raw IEEE 754 float32 deltas with a 1-byte magic identifier, 2-byte sequence counter, and 1-byte button bitmask, eliminating JSON parsing and string allocation overhead in the hot loop.

3. **Intelligent Coalescing:**  
   High-frequency touchscreen drag movements that arrive within sub-millisecond windows are coalesced to match the monitor's refresh rate (60Hz / 120Hz / 144Hz) preventing network buffer saturation.
