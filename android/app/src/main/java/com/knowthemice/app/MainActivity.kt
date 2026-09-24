package com.knowthemice.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.knowthemice.app.model.ConnectionState
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.network.ControlClient
import com.knowthemice.app.network.DiscoveryClient
import com.knowthemice.app.sensor.AirMouseEngine
import com.knowthemice.app.ui.screens.*
import com.knowthemice.app.ui.theme.BgAmoled
import com.knowthemice.app.ui.theme.KnowTheMiceTheme
import com.knowthemice.app.update.AndroidUpdateInfo
import com.knowthemice.app.update.UpdateManager
import kotlinx.coroutines.launch

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

        // Initialize UpdateManager with state persistence
        UpdateManager.initialize(this, BuildConfig.VERSION_CODE)
        lifecycleScope.launch {
            try {
                UpdateManager.checkForUpdates(this@MainActivity, BuildConfig.VERSION_CODE)
            } catch (_: Exception) {}
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
        val updateState by UpdateManager.updateState.collectAsState()

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
                    updateState = updateState,
                    onDownloadUpdate = { info ->
                        lifecycleScope.launch {
                            UpdateManager.downloadUpdate(this@MainActivity, info)
                        }
                    },
                    onInstallStagedUpdate = {
                        UpdateManager.installStagedUpdate(this@MainActivity)
                    },
                    onRetryUpdate = { info ->
                        lifecycleScope.launch {
                            if (info != null) {
                                UpdateManager.downloadUpdate(this@MainActivity, info)
                            } else {
                                UpdateManager.checkForUpdates(this@MainActivity, BuildConfig.VERSION_CODE)
                            }
                        }
                    },
                    onConnectHost = { host ->
                        controlClient.connect(
                            context = this@MainActivity,
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
                            context = this@MainActivity,
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
                    onDisconnect = {
                        controlClient.disconnect()
                        currentScreen = "home"
                    },
                    onNavigate = { screen -> currentScreen = screen }
                )
            }

            "keyboard" -> {
                KeyboardScreen(
                    onSendKey = { key, code, action ->
                        triggerHaptic()
                        if (action == "DOWN") {
                            controlClient.sendKeyDown(code, key)
                        } else if (action == "UP") {
                            controlClient.sendKeyUp(code, key)
                        } else {
                            controlClient.sendKey(key = key, code = code, action = "PRESS")
                        }
                    },
                    onSendTextInput = { text ->
                        triggerHaptic()
                        controlClient.sendTextInput(text)
                    },
                    onSendShortcut = { sc ->
                        triggerHaptic()
                        when (sc) {
                            "Ctrl + C" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x43, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Ctrl + V" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x56, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Ctrl + X" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x58, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Ctrl + Z" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x5A, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Ctrl + Y" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x59, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Ctrl + A" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKey(code = 0x41, action = "PRESS")
                                controlClient.sendKeyUp(0x11)
                            }
                            "Alt + Tab" -> {
                                controlClient.sendKeyDown(0x12)
                                controlClient.sendKey(code = 0x09, action = "PRESS")
                                controlClient.sendKeyUp(0x12)
                            }
                            "Alt + F4" -> {
                                controlClient.sendKeyDown(0x12)
                                controlClient.sendKey(code = 0x73, action = "PRESS")
                                controlClient.sendKeyUp(0x12)
                            }
                            "Win + D" -> {
                                controlClient.sendKeyDown(0x5B)
                                controlClient.sendKey(code = 0x44, action = "PRESS")
                                controlClient.sendKeyUp(0x5B)
                            }
                            "Win + E" -> {
                                controlClient.sendKeyDown(0x5B)
                                controlClient.sendKey(code = 0x45, action = "PRESS")
                                controlClient.sendKeyUp(0x5B)
                            }
                            "Win + L" -> {
                                controlClient.sendKeyDown(0x5B)
                                controlClient.sendKey(code = 0x4C, action = "PRESS")
                                controlClient.sendKeyUp(0x5B)
                            }
                            "Win + R" -> {
                                controlClient.sendKeyDown(0x5B)
                                controlClient.sendKey(code = 0x52, action = "PRESS")
                                controlClient.sendKeyUp(0x5B)
                            }
                            "Ctrl+Shift+Esc" -> {
                                controlClient.sendKeyDown(0x11)
                                controlClient.sendKeyDown(0x10)
                                controlClient.sendKey(code = 0x1B, action = "PRESS")
                                controlClient.sendKeyUp(0x10)
                                controlClient.sendKeyUp(0x11)
                            }
                            "Win + Print" -> {
                                controlClient.sendKeyDown(0x5B)
                                controlClient.sendKey(code = 0x2C, action = "PRESS")
                                controlClient.sendKeyUp(0x5B)
                            }
                        }
                    },
                    hapticsEnabled = hapticsEnabled,
                    onToggleHaptics = { hapticsEnabled = !hapticsEnabled },
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
                        } else {
                            Toast.makeText(this@MainActivity, "PIN verification failed: $msg", Toast.LENGTH_SHORT).show()
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
