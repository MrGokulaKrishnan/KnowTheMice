package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.model.ConnectionState
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.components.GradientButton
import com.knowthemice.app.ui.components.StatusBadge
import com.knowthemice.app.ui.theme.*

@Composable
fun HomeScreen(
    connectionState: ConnectionState,
    currentHost: DiscoveredHost?,
    latencyMs: Long,
    discoveredHosts: List<DiscoveredHost>,
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
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KNOW THE MICE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Local Wireless Controller",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            StatusBadge(
                isConnected = connectionState == ConnectionState.CONNECTED,
                label = if (connectionState == ConnectionState.CONNECTED) "● Connected" else "○ Searching"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Active Connected Host Card
        if (connectionState == ConnectionState.CONNECTED && currentHost != null) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentHost.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${currentHost.ip} · ${currentHost.os}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${latencyMs}ms",
                            color = BrandHighlight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
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
                            Text("Touchpad", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onDisconnect,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StatusError)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Disconnect")
                        }
                    }
                }
            }
        } else {
            // Discovered Computers Section
            Text(
                text = "AVAILABLE COMPUTERS",
                color = BrandHighlight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Scanning Local Wi-Fi...",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ensure Know The Mice Host is running on your PC",
                            color = TextSecondary,
                            fontSize = 12.sp,
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
                        GlassSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConnectHost(host) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Computer,
                                        contentDescription = null,
                                        tint = BrandHighlight,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
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

                                Text(
                                    text = "PAIR",
                                    color = BrandOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Connect Manually Option
        TextButton(
            onClick = { showManualDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Connect Manually by IP",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }

    if (showManualDialog) {
        var ipInput by remember { mutableStateOf("192.168.1.") }
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Connect Manually", color = Color.White) },
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
                TextButton(onClick = {
                    showManualDialog = false
                    onManualConnect(ipInput, 52841)
                }) {
                    Text("Connect", color = BrandHighlight)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }
}
