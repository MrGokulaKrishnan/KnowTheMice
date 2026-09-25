package com.knowthemice.app.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.knowthemice.app.model.ConnectionState
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.service.ConnectionService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ConnectionManager {

    private var appContext: Context? = null
    val controlClient = ControlClient()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _currentHost = MutableStateFlow<DiscoveredHost?>(null)
    val currentHost: StateFlow<DiscoveredHost?> = _currentHost.asStateFlow()

    val latencyMs: StateFlow<Long> = controlClient.latencyMs

    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var isManualDisconnect = false
    private var isScreenOn = true
    private var lastSavedHost: DiscoveredHost? = null

    // Exponential backoff intervals in milliseconds
    private val backoffIntervalsMs = listOf(1000L, 2000L, 4000L, 8000L, 15000L)
    private const val PERIODIC_INTERVAL_MS = 20000L

    fun initialize(context: Context) {
        if (appContext != null) return
        val app = context.applicationContext
        appContext = app

        // Restore last connected host from SharedPreferences
        val prefs = app.getSharedPreferences("ktm_prefs", Context.MODE_PRIVATE)
        val lastIp = prefs.getString("last_host_ip", null)
        val lastName = prefs.getString("last_host_name", null)
        val lastPort = prefs.getInt("last_host_port", 52841)
        if (!lastIp.isNullOrEmpty() && !lastName.isNullOrEmpty()) {
            lastSavedHost = DiscoveredHost(
                id = lastIp,
                name = lastName,
                ip = lastIp,
                port = lastPort
            )
            _currentHost.value = lastSavedHost
        }

        // Wire ControlClient drop callbacks
        controlClient.onConnectionDropped = {
            handleConnectionDropped()
        }

        // Register Screen ON / Screen OFF BroadcastReceiver
        val screenFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        app.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        isScreenOn = false
                        if (_connectionState.value == ConnectionState.CONNECTED) {
                            _connectionState.value = ConnectionState.BACKGROUND_CONNECTED
                        }
                    }
                    Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT -> {
                        isScreenOn = true
                        handleScreenWake()
                    }
                }
            }
        }, screenFilter)

        // Register Network Callback for instant reconnect when Wi-Fi re-establishes
        try {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val netReq = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            cm?.registerNetworkCallback(netReq, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    handleNetworkAvailable()
                }
            })
        } catch (_: Exception) {}

        // Listen to controlClient state
        scope.launch {
            controlClient.connectionState.collect { clientState ->
                when (clientState) {
                    ConnectionState.CONNECTED -> {
                        val finalState = if (isScreenOn) ConnectionState.CONNECTED else ConnectionState.BACKGROUND_CONNECTED
                        _connectionState.value = finalState
                        cancelReconnect()
                        _currentHost.value?.let { host ->
                            saveLastHost(host)
                            appContext?.let { ctx ->
                                ConnectionService.startService(ctx, host.name)
                            }
                        }
                    }
                    ConnectionState.PAIRING -> {
                        _connectionState.value = ConnectionState.PAIRING
                    }
                    ConnectionState.AUTHENTICATING -> {
                        _connectionState.value = ConnectionState.AUTHENTICATING
                    }
                    ConnectionState.CONNECTING -> {
                        if (_connectionState.value != ConnectionState.RECONNECTING) {
                            _connectionState.value = ConnectionState.CONNECTING
                        }
                    }
                    ConnectionState.ERROR, ConnectionState.DISCONNECTED -> {
                        if (!isManualDisconnect && lastSavedHost != null && _connectionState.value != ConnectionState.RECONNECTING) {
                            triggerAutoReconnect()
                        } else if (isManualDisconnect) {
                            _connectionState.value = ConnectionState.DISCONNECTED
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun connect(
        host: DiscoveredHost,
        onPairingNeeded: () -> Unit = {},
        onConnected: () -> Unit = {}
    ) {
        val ctx = appContext ?: return
        isManualDisconnect = false
        cancelReconnect()
        _currentHost.value = host
        lastSavedHost = host
        saveLastHost(host)

        _connectionState.value = ConnectionState.CONNECTING
        controlClient.connect(
            context = ctx,
            host = host,
            onPairingNeeded = {
                _connectionState.value = ConnectionState.PAIRING
                onPairingNeeded()
            },
            onConnected = {
                _connectionState.value = if (isScreenOn) ConnectionState.CONNECTED else ConnectionState.BACKGROUND_CONNECTED
                cancelReconnect()
                ConnectionService.startService(ctx, host.name)
                onConnected()
            }
        )
    }

    fun disconnect() {
        isManualDisconnect = true
        cancelReconnect()
        controlClient.disconnect(isManual = true)
        _connectionState.value = ConnectionState.DISCONNECTED
        appContext?.let { ConnectionService.stopService(it) }
    }

    fun submitPairingPin(pin: String, onResult: (Boolean, String) -> Unit) {
        controlClient.submitPairingPin(pin) { success, msg ->
            if (success) {
                _connectionState.value = if (isScreenOn) ConnectionState.CONNECTED else ConnectionState.BACKGROUND_CONNECTED
                cancelReconnect()
                _currentHost.value?.let { host ->
                    saveLastHost(host)
                    appContext?.let { ctx -> ConnectionService.startService(ctx, host.name) }
                }
            }
            onResult(success, msg)
        }
    }

    private fun handleConnectionDropped() {
        if (isManualDisconnect) return
        triggerAutoReconnect()
    }

    private fun handleScreenWake() {
        if (_connectionState.value == ConnectionState.BACKGROUND_CONNECTED) {
            if (controlClient.isSocketAlive()) {
                _connectionState.value = ConnectionState.CONNECTED
            } else {
                // Socket died while in background; immediately recover session
                triggerAutoReconnect(instant = true)
            }
        } else if (_connectionState.value == ConnectionState.RECONNECTING) {
            // Trigger instant retry on wake
            triggerAutoReconnect(instant = true)
        }
    }

    private fun handleNetworkAvailable() {
        if (!isManualDisconnect && (_connectionState.value == ConnectionState.RECONNECTING || _connectionState.value == ConnectionState.DISCONNECTED || _connectionState.value == ConnectionState.ERROR)) {
            triggerAutoReconnect(instant = true)
        }
    }

    @Synchronized
    private fun triggerAutoReconnect(instant: Boolean = false) {
        if (isManualDisconnect) return
        val targetHost = lastSavedHost ?: _currentHost.value ?: return

        reconnectJob?.cancel()
        _connectionState.value = ConnectionState.RECONNECTING

        reconnectJob = scope.launch {
            var attempt = 0
            while (isActive && !isManualDisconnect && _connectionState.value == ConnectionState.RECONNECTING) {
                val delayMs = if (instant && attempt == 0) {
                    100L
                } else if (attempt < backoffIntervalsMs.size) {
                    backoffIntervalsMs[attempt]
                } else {
                    PERIODIC_INTERVAL_MS
                }

                delay(delayMs)
                attempt++

                if (!isActive || isManualDisconnect) break

                val ctx = appContext ?: break
                var connectedSuccessfully = false

                val connectDone = CompletableDeferred<Boolean>()
                controlClient.connect(
                    context = ctx,
                    host = targetHost,
                    onPairingNeeded = {
                        _connectionState.value = ConnectionState.PAIRING
                        connectDone.complete(false)
                    },
                    onConnected = {
                        connectedSuccessfully = true
                        connectDone.complete(true)
                    }
                )

                // Wait up to 3.5s for connect result
                withTimeoutOrNull(3500) {
                    connectDone.await()
                }

                if (connectedSuccessfully) {
                    _connectionState.value = if (isScreenOn) ConnectionState.CONNECTED else ConnectionState.BACKGROUND_CONNECTED
                    ConnectionService.startService(ctx, targetHost.name)
                    break
                }
            }
        }
    }

    private fun cancelReconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
    }

    private fun saveLastHost(host: DiscoveredHost) {
        lastSavedHost = host
        appContext?.getSharedPreferences("ktm_prefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putString("last_host_ip", host.ip)
            putString("last_host_name", host.name)
            putInt("last_host_port", host.port)
            apply()
        }
    }
}
