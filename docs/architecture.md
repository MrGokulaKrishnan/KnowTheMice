# KNOW THE MICE — System Architecture

## 1. Executive Architecture Summary

Know The Mice is a production-grade, zero-cloud remote-control ecosystem designed to allow Android smartphones to operate as high-precision wireless input peripherals (touchpad, keyboard, gyroscope air mouse, media controller, presentation clicker) for Windows PCs over a local area network (LAN).

```mermaid
graph LR
    subgraph MobileDevice["Android Client"]
        Compose["Jetpack Compose UI\n(Liquid Glass)"]
        SensorEng["Sensor Fusion Engine\n(Gyro + Accel)"]
        TouchEng["Gesture Recognizer\n(Touch, Tap, Drag)"]
        NetClient["Control Client\n(TCP Client, Port 52841)"]
        DiscClient["Discovery Client\n(UDP Broadcast, Port 52840)"]
    end

    subgraph Transport["Local Area Network (Zero Cloud)"]
        UDP["UDP Broadcast\n(Port 52840)"]
        TCP["TCP Stream / Binary + JSON\n(Port 52841)"]
    end

    subgraph DesktopHost["Windows Host (.NET 8)"]
        DiscBeacon["Discovery Beacon\n(UDP Broadcaster)"]
        NetServer["Network Server\n(TCP Listener)"]
        PinMgr["PIN Manager\n(X25519 + 60s TTL)"]
        InputInj["Win32 SendInput Injector"]
        SysCtrl["Power & Whitelist Launcher"]
        Tray["WPF System Tray Dashboard"]
    end

    DiscClient -.-> UDP -.-> DiscBeacon
    NetClient <===> TCP <===> NetServer
    NetServer --> InputInj
    NetServer --> SysCtrl
    NetServer <--> PinMgr
    Tray --> NetServer
```

---

## 2. Core Subsystems

### 2.1 Low-Latency Motion Framing
High-frequency mouse inputs (100+ packets/second) are packed into a 13-byte binary payload to minimize serialization overhead and avoid TCP buffer latency:
- Byte 0: Magic byte (`0x01`)
- Bytes 1-4: Delta X (`float32`, Little Endian)
- Bytes 5-8: Delta Y (`float32`, Little Endian)
- Bytes 9-10: Sequence Number (`uint16`, Little Endian)
- Byte 11: Button state flags (`uint8`)
- Byte 12: XOR Checksum (`uint8`)

### 2.2 System Tray Host Architecture
The Windows Host executes as a background desktop utility with a persistent system tray notification icon (`NotifyIcon`), minimizing to the tray on close and maintaining active TCP/UDP listeners on background worker threads.

### 2.3 Sensor Fusion Air Mouse
The Android IMU processor samples `Sensor.TYPE_GYROSCOPE` at `SENSOR_DELAY_GAME` (~50Hz). Rotational velocity around the Z and X axes is integrated through a dead-zone noise filter, providing smooth hand-waving cursor control with zero drift.
