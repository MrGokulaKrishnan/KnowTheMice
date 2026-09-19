package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.ui.theme.*

@Composable
fun PowerDialog(
    targetPcName: String,
    onExecutePower: (action: String) -> Unit,
    onDismiss: () -> Unit
) {
    var pendingAction by remember { mutableStateOf<String?>(null) }

    if (pendingAction != null) {
        // Confirmation Step 2
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = {
                Text("Confirm $pendingAction", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you absolutely sure you want to $pendingAction $targetPcName?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val action = pendingAction!!
                        pendingAction = null
                        onExecutePower(action)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CONFIRM", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    } else {
        // Power Actions Menu
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "SYSTEM POWER CONTROLS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("Lock Workstation", "LOCK", Icons.Default.Lock),
                        Triple("Sleep Mode", "SLEEP", Icons.Default.Bedtime),
                        Triple("Restart Computer", "RESTART", Icons.Default.RestartAlt),
                        Triple("Shut Down Computer", "SHUTDOWN", Icons.Default.PowerSettingsNew)
                    ).forEach { (label, act, icon) ->
                        Button(
                            onClick = { pendingAction = act },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (act == "SHUTDOWN") Color(0x30EF4444) else Color(0x18FFFFFF)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = if (act == "SHUTDOWN") StatusError else BrandHighlight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    label,
                                    color = if (act == "SHUTDOWN") StatusError else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }
}
