package com.knowthemice.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.R
import com.knowthemice.app.model.ConnectionState
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.components.StatusBadge
import com.knowthemice.app.ui.theme.*
import com.knowthemice.app.update.AndroidUpdateInfo
import com.knowthemice.app.update.AndroidUpdateState

@Composable
fun HomeScreen(
    connectionState: ConnectionState,
    currentHost: DiscoveredHost?,
    latencyMs: Long,
    discoveredHosts: List<DiscoveredHost>,
    updateState: AndroidUpdateState = AndroidUpdateState.Idle,
    onDownloadUpdate: (AndroidUpdateInfo) -> Unit = {},
    onInstallStagedUpdate: () -> Unit = {},
    onRetryUpdate: (AndroidUpdateInfo?) -> Unit = {},
    onConnectHost: (DiscoveredHost) -> Unit,
    onDisconnect: () -> Unit,
    onManualConnect: (String, Int) -> Unit,
    onNavigate: (String) -> Unit
) {
    var showManualDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        // Brand Header with Modern KM Logo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_monogram),
                        contentDescription = "KM Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "KNOW THE MICE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 1.2.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Wireless PC Remote Controller",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    isConnected = connectionState == ConnectionState.CONNECTED,
                    label = if (connectionState == ConnectionState.CONNECTED) "Connected" else "Scanning"
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { onNavigate("settings") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Explicit OTA Update State Banner
        val isUpdateBannerVisible = updateState !is AndroidUpdateState.Idle && updateState !is AndroidUpdateState.UpToDate
        AnimatedVisibility(visible = isUpdateBannerVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0x35FF5E00), Color(0x15FFA550))
                        )
                    )
                    .border(1.5.dp, BrandHighlight, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                when (updateState) {
                    is AndroidUpdateState.Checking -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = BrandHighlight,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Checking for updates...",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    is AndroidUpdateState.UpdateAvailable -> {
                        val update = updateState.info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = BrandHighlight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UPDATE AVAILABLE v${update.version}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = if (update.changelog.isNotBlank()) update.changelog else "New features and optimizations",
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Button(
                                onClick = { onDownloadUpdate(update) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "DOWNLOAD",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    is AndroidUpdateState.Downloading -> {
                        val update = updateState.info
                        val pct = updateState.progressPct
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "DOWNLOADING v${update.version}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Please wait while update is prepared ($pct%)...",
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            CircularProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier.size(32.dp),
                                color = BrandHighlight,
                                trackColor = Color(0x30FFFFFF)
                            )
                        }
                    }

                    is AndroidUpdateState.Ready -> {
                        val update = updateState.info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UPDATE READY v${update.version}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = "Restart the app to finish installing update.",
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Button(
                                onClick = onInstallStagedUpdate,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "RESTART TO UPDATE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    is AndroidUpdateState.Failed -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = StatusError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UPDATE FAILED",
                                        color = StatusError,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = updateState.error,
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Button(
                                onClick = { onRetryUpdate(updateState.info) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x30FFFFFF)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "RETRY",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Connected Host Card
        if (connectionState == ConnectionState.CONNECTED && currentHost != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F131C))
                    .border(1.5.dp, Color(0x4022C55E), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(StatusSuccess)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentHost.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "${currentHost.ip} · Windows Host",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = "${latencyMs}ms",
                            color = BrandHighlight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("remote") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Remote", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onDisconnect,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(StatusError)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Disconnect")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section Title: Available Computers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AVAILABLE COMPUTERS",
                color = BrandHighlight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            TextButton(
                onClick = { showManualDialog = true },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+ Manual IP",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (discoveredHosts.isEmpty()) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = BrandHighlight,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Searching on Wi-Fi...",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Ensure Know The Mice Host is running on your PC",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(discoveredHosts) { host ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1017))
                            .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(14.dp))
                            .clickable { onConnectHost(host) }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x15FF5E00)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Computer,
                                        contentDescription = null,
                                        tint = BrandHighlight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = host.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${host.ip}:${host.port}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { onConnectHost(host) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("CONNECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (showManualDialog) {
        var ipInput by remember { mutableStateOf("192.168.1.") }
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Connect Manually", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter PC's local IP address:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = { ipInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandHighlight
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManualDialog = false
                        onManualConnect(ipInput, 52841)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight)
                ) {
                    Text("Connect", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF0F1218)
        )
    }
}
