package com.knowthemice.app.network

import android.content.Context
import com.google.gson.Gson
import com.knowthemice.app.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ControlClient {
    private val gson = Gson()
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var scope: CoroutineScope? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _currentHost = MutableStateFlow<DiscoveredHost?>(null)
    val currentHost = _currentHost.asStateFlow()

    private val _latencyMs = MutableStateFlow(8L)
    val latencyMs = _latencyMs.asStateFlow()

    private var sequenceNumber: Int = 0
    private var pendingPinCallback: ((Boolean, String) -> Unit)? = null
    private val clientId = "android-" + (android.os.Build.MODEL ?: "device").replace(" ", "_")

    fun connect(context: Context, host: DiscoveredHost, onPairingNeeded: () -> Unit, onConnected: () -> Unit) {
        disconnect()
        _currentHost.value = host
        _connectionState.value = ConnectionState.CONNECTING
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope?.launch {
            try {
                val s = Socket().apply {
                    tcpNoDelay = true // Vital for sub-15ms input streaming!
                    connect(InetSocketAddress(host.ip, host.port), 4000)
                }
                socket = s
                inputStream = s.getInputStream()
                outputStream = s.getOutputStream()

                val prefs = context.getSharedPreferences("ktm_prefs", Context.MODE_PRIVATE)
                val savedToken = prefs.getString("token_${host.ip}", null)

                if (!savedToken.isNullOrEmpty()) {
                    // Fast path: Authenticate with saved token
                    val authMsg = AuthMessage(
                        clientId = clientId,
                        authToken = savedToken
                    )
                    sendJsonMessage(authMsg)
                } else {
                    // Initial pairing request
                    val pairReq = PairRequestMessage(
                        clientName = android.os.Build.MODEL ?: "Android Device",
                        clientId = clientId
                    )
                    sendJsonMessage(pairReq)
                }

                // Listen for incoming responses (Ping/Pong, Auth, etc.)
                listenLoop(context, onPairingNeeded, onConnected)
            } catch (ex: Exception) {
                _connectionState.value = ConnectionState.ERROR
            }
        }
    }

    fun submitPairingPin(pin: String, onResult: (Boolean, String) -> Unit) {
        pendingPinCallback = onResult
        scope?.launch {
            try {
                val verify = PairVerifyMessage(
                    clientId = clientId,
                    pin = pin
                )
                sendJsonMessage(verify)
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, ex.message ?: "Network error")
                }
            }
        }
    }

    private suspend fun listenLoop(context: Context, onPairingNeeded: () -> Unit, onConnected: () -> Unit) {
        val header = ByteArray(4)
        val prefs = context.getSharedPreferences("ktm_prefs", Context.MODE_PRIVATE)

        while (scope?.isActive == true && socket?.isConnected == true) {
            try {
                var read = 0
                while (read < 4) {
                    val r = inputStream?.read(header, read, 4 - read) ?: -1
                    if (r == -1) return
                    read += r
                }
                val length = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).int
                if (length <= 0 || length > 65536) return

                val body = ByteArray(length)
                read = 0
                while (read < length) {
                    val r = inputStream?.read(body, read, length - read) ?: -1
                    if (r == -1) return
                    read += r
                }

                val json = String(body, Charsets.UTF_8)
                val resp = gson.fromJson(json, BaseMessage::class.java)

                when (resp.type) {
                    "AUTH_SUCCESS" -> {
                        _connectionState.value = ConnectionState.CONNECTED
                        withContext(Dispatchers.Main) {
                            onConnected()
                        }
                        startHeartbeat()
                    }
                    "AUTH_FAILED" -> {
                        // Token invalid/expired; clear token and request pairing
                        val currentIp = _currentHost.value?.ip
                        if (currentIp != null) {
                            prefs.edit().remove("token_$currentIp").apply()
                        }
                        val pairReq = PairRequestMessage(
                            clientName = android.os.Build.MODEL ?: "Android Device",
                            clientId = clientId
                        )
                        sendJsonMessage(pairReq)
                    }
                    "PAIR_RESPONSE" -> {
                        val pairResp = gson.fromJson(json, PairResponseMessage::class.java)
                        if (pairResp.success) {
                            val currentIp = _currentHost.value?.ip
                            if (currentIp != null && pairResp.authToken.isNotEmpty()) {
                                prefs.edit().putString("token_$currentIp", pairResp.authToken).apply()
                            }
                            _connectionState.value = ConnectionState.CONNECTED
                            withContext(Dispatchers.Main) {
                                pendingPinCallback?.invoke(true, pairResp.message)
                                pendingPinCallback = null
                                onConnected()
                            }
                            startHeartbeat()
                        } else {
                            if (pairResp.message == "PIN_REQUIRED") {
                                _connectionState.value = ConnectionState.PAIRING
                                withContext(Dispatchers.Main) {
                                    onPairingNeeded()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    pendingPinCallback?.invoke(false, pairResp.message)
                                    pendingPinCallback = null
                                }
                            }
                        }
                    }
                    "PAIR_PIN_REQUIRED" -> {
                        _connectionState.value = ConnectionState.PAIRING
                        withContext(Dispatchers.Main) {
                            onPairingNeeded()
                        }
                    }
                    "PONG" -> {
                        val rtt = System.currentTimeMillis() - resp.timestamp
                        _latencyMs.value = Math.max(2L, rtt)
                    }
                }
            } catch (_: Exception) {
                break
            }
        }
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    private fun startHeartbeat() {
        scope?.launch {
            while (isActive && socket?.isConnected == true) {
                delay(2000)
                try {
                    sendJsonMessage(BaseMessage(type = "PING", timestamp = System.currentTimeMillis()))
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Ultra-compact 13-byte binary motion framing for high-frequency mouse events
     */
    fun sendMouseMove(dx: Float, dy: Float, leftDown: Boolean = false, rightDown: Boolean = false, drag: Boolean = false) {
        if (socket?.isConnected != true) return
        scope?.launch(Dispatchers.IO) {
            try {
                sequenceNumber = (sequenceNumber + 1) and 0xFFFF
                var flags = 0
                if (leftDown) flags = flags or 0x01
                if (rightDown) flags = flags or 0x02
                if (drag) flags = flags or 0x08

                val buffer = ByteBuffer.allocate(17).order(ByteOrder.LITTLE_ENDIAN)
                // 4-byte length prefix (13 bytes body)
                buffer.putInt(13)
                // Body (13 bytes)
                buffer.put(0x01.toByte()) // Magic
                buffer.putFloat(dx)
                buffer.putFloat(dy)
                buffer.putShort(sequenceNumber.toShort())
                buffer.put(flags.toByte())

                // Checksum
                var checksum: Byte = 0
                val array = buffer.array()
                for (i in 4 until 16) {
                    checksum = (checksum.toInt() xor array[i].toInt()).toByte()
                }
                buffer.put(checksum)

                outputStream?.write(buffer.array())
                outputStream?.flush()
            } catch (_: Exception) {}
        }
    }

    fun sendMouseClick(button: String, action: String = "CLICK") {
        sendJsonMessage(MouseClickMessage(button = button.uppercase(), action = action.uppercase()))
    }

    fun sendMouseScroll(dx: Float, dy: Float) {
        sendJsonMessage(MouseScrollMessage(dx = dx, dy = dy))
    }

    fun sendKey(key: String = "", code: Int = 0, action: String = "PRESS", modifiers: List<String> = emptyList()) {
        sendJsonMessage(KeyInputMessage(key = key, code = code, action = action.uppercase(), modifiers = modifiers))
    }

    fun sendKeyDown(code: Int, key: String = "") {
        sendJsonMessage(mapOf("type" to "KEY_DOWN", "code" to code, "key" to key))
    }

    fun sendKeyUp(code: Int, key: String = "") {
        sendJsonMessage(mapOf("type" to "KEY_UP", "code" to code, "key" to key))
    }

    fun sendTextInput(text: String) {
        if (text.isNotEmpty()) {
            sendJsonMessage(mapOf("type" to "TEXT_INPUT", "text" to text))
        }
    }

    fun sendMedia(action: String) {
        sendJsonMessage(MediaMessage(action = action.uppercase()))
    }

    fun sendPresentation(action: String) {
        sendJsonMessage(PresentationMessage(action = action.uppercase()))
    }

    fun sendPower(action: String, targetPc: String) {
        sendJsonMessage(PowerMessage(action = action.uppercase(), confirmed = true, targetPc = targetPc))
    }

    fun sendAppLaunch(appId: String) {
        sendJsonMessage(mapOf("type" to "APP_LAUNCH", "appId" to appId))
    }

    private fun sendJsonMessage(message: Any) {
        scope?.launch(Dispatchers.IO) {
            try {
                val jsonBytes = gson.toJson(message).toByteArray(Charsets.UTF_8)
                val header = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(jsonBytes.size).array()
                outputStream?.write(header)
                outputStream?.write(jsonBytes)
                outputStream?.flush()
            } catch (_: Exception) {}
        }
    }

    fun disconnect() {
        scope?.cancel()
        scope = null
        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
