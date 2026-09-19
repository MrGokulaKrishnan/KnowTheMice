package com.knowthemice.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.knowthemice.app.model.ConnectionState
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.network.ControlClient
import com.knowthemice.app.network.DiscoveryClient
import com.knowthemice.app.sensor.AirMouseEngine
import com.knowthemice.app.ui.screens.*
import com.knowthemice.app.ui.theme.BgAmoled
import com.knowthemice.app.ui.theme.KnowTheMiceTheme

class MainActivity : ComponentActivity() {

    private lateinit var discoveryClient: DiscoveryClient
    private val controlClient = ControlClient()
    private var airMouseEngine: AirMouseEngine? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        discoveryClient = DiscoveryClient(this)
        discoveryClient.startDiscovery()

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        airMouseEngine = AirMouseEngine(this) { dx, dy ->
            controlClient.sendMouseMove(dx, dy)
        }

        setContent {
            KnowTheMiceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BgAmoled) {
                    MainAppContent()
                }
            }
        }
    }

    @Composable
    private fun MainAppContent() {
        var currentScreen by remember { mutableStateOf("home") }
        var showPairingDialog by remember { mutableStateOf(false) }
        var showPowerDialog by remember { mutableStateOf(false) }

        var sensitivity by remember { mutableStateOf(1.2f) }
        var naturalScroll by remember { mutableStateOf(true) }
        var hapticsEnabled by remember { mutableStateOf(true) }
        var isAirMouseActive by remember { mutableStateOf(false) }

        val connectionState by controlClient.connectionState.collectAsState()
        val currentHost by controlClient.currentHost.collectAsState()
        val latencyMs by controlClient.latencyMs.collectAsState()
        val hostsMap by discoveryClient.discoveredHosts.collectAsState()
        val hostList = hostsMap.values.toList()

        fun triggerHaptic() {
            if (!hapticsEnabled) return
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            } catch (_: Exception) {}
        }

        when (currentScreen) {
            "home" -> {
                HomeScreen(
                    connectionState = connectionState,
                    currentHost = currentHost,
                    latencyMs = latencyMs,
                    discoveredHosts = hostList,
                    onConnectHost = { host ->
                        controlClient.connect(
                            host = host,
                            onPairingNeeded = { showPairingDialog = true },
                            onConnected = {
                                showPairingDialog = false
                                currentScreen = "remote"
                            }
                        )
                    },
                    onDisconnect = {
                        controlClient.disconnect()
                    },
                    onManualConnect = { ip, port ->
                        val manualHost = DiscoveredHost(
                            id = ip,
                            name = "Manual PC",
                            ip = ip,
                            port = port
                        )
                        controlClient.connect(
                            host = manualHost,
                            onPairingNeeded = { showPairingDialog = true },
                            onConnected = {
                                showPairingDialog = false
                                currentScreen = "remote"
                            }
                        )
                    },
                    onNavigate = { screen -> currentScreen = screen }
                )
            }

            "remote" -> {
                RemotePadScreen(
                    currentHost = currentHost,
                    latencyMs = latencyMs,
                    isAirMouseActive = isAirMouseActive,
                    onMouseMove = { dx, dy ->
                        controlClient.sendMouseMove(dx * sensitivity, dy * sensitivity)
                    },
                    onMouseClick = { btn, act ->
                        triggerHaptic()
                        controlClient.sendMouseClick(btn, act)
                    },
                    onMouseScroll = { dx, dy ->
                        val mult = if (naturalScroll) 1f else -1f
                        controlClient.sendMouseScroll(dx, dy * mult)
                    },
                    onToggleAirMouse = {
                        isAirMouseActive = !isAirMouseActive
                        if (isAirMouseActive) {
                            airMouseEngine?.start()
                        } else {
                            airMouseEngine?.stop()
                        }
                    },
                    onOpenPower = { showPowerDialog = true },
                    onNavigate = { screen -> currentScreen = screen }
                )
            }

            "keyboard" -> {
                KeyboardScreen(
                    onSendKey = { key, code ->
                        triggerHaptic()
                        controlClient.sendKey(key = key, code = code)
                    },
                    onSendShortcut = { sc ->
                        triggerHaptic()
                        when (sc) {
                            "ALT + TAB" -> {
                                controlClient.sendKey(code = 0x12, action = "DOWN")
                                controlClient.sendKey(code = 0x09, action = "PRESS")
                                controlClient.sendKey(code = 0x12, action = "UP")
                            }
                            "WIN + D" -> {
                                controlClient.sendKey(code = 0x5B, action = "DOWN")
                                controlClient.sendKey(key = "d", action = "PRESS")
                                controlClient.sendKey(code = 0x5B, action = "UP")
                            }
                            "CTRL + Z" -> {
                                controlClient.sendKey(code = 0x11, action = "DOWN")
                                controlClient.sendKey(key = "z", action = "PRESS")
                                controlClient.sendKey(code = 0x11, action = "UP")
                            }
                            "WIN + L" -> {
                                controlClient.sendKey(code = 0x5B, action = "DOWN")
                                controlClient.sendKey(key = "l", action = "PRESS")
                                controlClient.sendKey(code = 0x5B, action = "UP")
                            }
                        }
                    },
                    onBack = { currentScreen = "remote" }
                )
            }

            "media" -> {
                MediaScreen(
                    onMediaCommand = { cmd ->
                        triggerHaptic()
                        controlClient.sendMedia(cmd)
                    },
                    onBack = { currentScreen = "remote" }
                )
            }

            "presentation" -> {
                PresentationScreen(
                    onPresentationCommand = { cmd ->
                        triggerHaptic()
                        controlClient.sendPresentation(cmd)
                    },
                    onBack = { currentScreen = "remote" }
                )
            }

            "settings" -> {
                SettingsScreen(
                    mouseSensitivity = sensitivity,
                    onSensitivityChange = { sensitivity = it },
                    naturalScrolling = naturalScroll,
                    onNaturalScrollingChange = { naturalScroll = it },
                    hapticFeedback = hapticsEnabled,
                    onHapticFeedbackChange = { hapticsEnabled = it },
                    onBack = { currentScreen = "home" }
                )
            }
        }

        // Modals
        if (showPairingDialog && currentHost != null) {
            PairingDialog(
                hostName = currentHost?.name ?: "PC",
                onSubmitPin = { pin ->
                    controlClient.submitPairingPin(pin) { success, msg ->
                        if (success) {
                            showPairingDialog = false
                            currentScreen = "remote"
                        }
                    }
                },
                onDismiss = { showPairingDialog = false }
            )
        }

        if (showPowerDialog && currentHost != null) {
            PowerDialog(
                targetPcName = currentHost?.name ?: "PC",
                onExecutePower = { act ->
                    controlClient.sendPower(act, currentHost?.name ?: "")
                },
                onDismiss = { showPowerDialog = false }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        discoveryClient.stopDiscovery()
        controlClient.disconnect()
        airMouseEngine?.stop()
    }
}
