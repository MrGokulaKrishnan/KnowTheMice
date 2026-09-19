# KNOW THE MICE — Security & Threat Model

## 1. Threat Model & Principles

1. **Zero-Cloud / Local Subnet Only:**  
   The application communicates strictly across local network interfaces. No data packets are relayed across remote WAN servers.

2. **Zero Shell Execution:**  
   The Windows host explicitly excludes any facility for command execution. `cmd.exe`, `powershell.exe`, WSL, and arbitrary binary execution are banned at the parser level. The Application Launcher only executes pre-approved, whitelisted installed applications.

3. **No Credential / Keystroke Logging:**  
   Diagnostic logging records only connection state transitions, timestamps, and packet metrics. Keystroke scan codes, clipboard strings, passwords, and cryptographic keys are never written to logs or disk.

4. **Cryptographic Identity & DPAPI Storage:**  
   Windows pairing tokens are encrypted at rest using the Windows Data Protection API (DPAPI) with `CurrentUser` scope. Android client tokens are stored in the hardware-backed Android Keystore.

---

## 2. Cryptographic Handshake Specification

- **Key Exchange:** Ephemeral Diffie-Hellman Key Exchange (X25519 / NIST P-256)
- **PIN Verification:** 6-digit cryptographically random numeric code with a 60-second Time-To-Live (TTL).
- **Transport Cipher:** AES-256-GCM (Galois/Counter Mode) authenticated encryption with 128-bit authentication tags and 96-bit nonces.
- **Replay Protection:** 32-bit monotonic sequence counter with a sliding replay window of 128 packets.
- **Lockout Enforcer:** 3 consecutive failed PIN attempts trigger a 5-minute cooldown period.
