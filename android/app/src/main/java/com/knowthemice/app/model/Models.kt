package com.knowthemice.app.model

enum class ConnectionState {
    DISCOVERING,
    PAIRING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTED,
    BLOCKED,
    ERROR
}

data class DiscoveredHost(
    val id: String,
    val name: String,
    val ip: String,
    val port: Int = 52841,
    val os: String = "Windows",
    val latencyMs: Long = 0,
    val lastSeen: Long = System.currentTimeMillis()
)

data class BaseMessage(
    val type: String,
    val seq: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class PairRequestMessage(
    val type: String = "PAIR_REQUEST",
    val clientName: String,
    val clientId: String,
    val clientPub: String = ""
)

data class PairVerifyMessage(
    val type: String = "PAIR_VERIFY",
    val clientId: String,
    val pin: String
)

data class PairResponseMessage(
    val type: String = "PAIR_RESPONSE",
    val success: Boolean,
    val authToken: String = "",
    val message: String = ""
)

data class AuthMessage(
    val type: String = "AUTH",
    val clientId: String,
    val authToken: String
)

data class MouseClickMessage(
    val type: String = "MOUSE_CLICK",
    val button: String = "LEFT",
    val action: String = "CLICK"
)

data class MouseScrollMessage(
    val type: String = "MOUSE_SCROLL",
    val dx: Float,
    val dy: Float
)

data class KeyInputMessage(
    val type: String = "KEY_INPUT",
    val key: String = "",
    val code: Int = 0,
    val action: String = "PRESS",
    val modifiers: List<String> = emptyList()
)

data class MediaMessage(
    val type: String = "MEDIA_CMD",
    val action: String
)

data class PresentationMessage(
    val type: String = "PRESENTATION_CMD",
    val action: String
)

data class PowerMessage(
    val type: String = "POWER_CMD",
    val action: String,
    val confirmed: Boolean = true,
    val targetPc: String
)
