package com.knowthemice.app.network

import android.content.Context
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.knowthemice.app.model.DiscoveredHost
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DiscoveryClient(private val context: Context) {
    private val gson = Gson()
    private val _discoveredHosts = MutableStateFlow<Map<String, DiscoveredHost>>(emptyMap())
    val discoveredHosts = _discoveredHosts.asStateFlow()

    private var socket: DatagramSocket? = null
    private var scope: CoroutineScope? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    fun startDiscovery() {
        if (scope != null) return
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        try {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            multicastLock = wifi?.createMulticastLock("KnowTheMiceLock")?.apply {
                setReferenceCounted(true)
                acquire()
            }
        } catch (_: Exception) {}

        scope?.launch {
            try {
                socket = DatagramSocket(52840).apply {
                    broadcast = true
                    reuseAddress = true
                }

                // Send initial discovery probe
                launch {
                    val probeJson = """{"service":"know-the-mice","type":"DISCOVER"}"""
                    val probeBytes = probeJson.toByteArray()
                    val target = InetAddress.getByName("255.255.255.255")
                    val packet = DatagramPacket(probeBytes, probeBytes.size, target, 52840)
                    socket?.send(packet)
                }

                val buffer = ByteArray(4096)
                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)

                    val jsonStr = String(packet.data, 0, packet.length)
                    val senderIp = packet.address.hostAddress ?: continue

                    try {
                        val obj = gson.fromJson(jsonStr, JsonObject::class.java)
                        if (obj.has("service") && obj.get("service").asString == "know-the-mice") {
                            val id = obj.get("id")?.asString ?: senderIp
                            val name = obj.get("name")?.asString ?: "Windows PC"
                            val port = obj.get("port")?.asInt ?: 52841
                            val os = obj.get("os")?.asString ?: "Windows"

                            val host = DiscoveredHost(
                                id = id,
                                name = name,
                                ip = senderIp,
                                port = port,
                                os = os,
                                lastSeen = System.currentTimeMillis()
                            )

                            _discoveredHosts.value = _discoveredHosts.value + (id to host)
                        }
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
            } finally {
                socket?.close()
            }
        }
    }

    fun stopDiscovery() {
        scope?.cancel()
        scope = null
        socket?.close()
        socket = null
        try {
            if (multicastLock?.isHeld == true) {
                multicastLock?.release()
            }
        } catch (_: Exception) {}
    }
}
