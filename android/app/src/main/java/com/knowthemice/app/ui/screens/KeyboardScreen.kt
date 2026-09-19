package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.theme.*

@Composable
fun KeyboardScreen(
    onSendKey: (key: String, code: Int) -> Unit,
    onSendShortcut: (name: String) -> Unit,
    onBack: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val modifiers = remember { mutableStateMapOf("CTRL" to false, "ALT" to false, "SHIFT" to false, "WIN" to false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("WIRELESS KEYBOARD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            IconButton(onClick = { textInput = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live text buffer input field
        OutlinedTextField(
            value = textInput,
            onValueChange = { newVal ->
                if (newVal.length > textInput.length) {
                    val addedChar = newVal.last().toString()
                    onSendKey(addedChar, 0)
                }
                textInput = newVal
            },
            placeholder = { Text("Type here to send keystrokes...", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = BrandHighlight,
                unfocusedBorderColor = GlassBorder
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Modifier Keys Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CTRL", "ALT", "SHIFT", "WIN").forEach { mod ->
                val isActive = modifiers[mod] == true
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) BrandHighlight else Color(0x15FFFFFF))
                        .clickable {
                            modifiers[mod] = !isActive
                            val vk = when (mod) {
                                "CTRL" -> 0x11
                                "ALT" -> 0x12
                                "SHIFT" -> 0x10
                                "WIN" -> 0x5B
                                else -> 0
                            }
                            onSendKey("", vk)
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mod, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Essential Control Keys Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("ESC", 0x1B, 1f),
                Triple("TAB", 0x09, 1f),
                Triple("BKSP", 0x08, 1.2f),
                Triple("ENTER", 0x0D, 1.4f)
            ).forEach { (label, vk, weight) ->
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x18FFFFFF))
                        .clickable { onSendKey("", vk) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Common Quick Shortcuts
        Text("COMMON WINDOWS SHORTCUTS", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALT + TAB", "WIN + D", "CTRL + Z", "WIN + L").forEach { sc ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x10FFFFFF))
                        .clickable { onSendShortcut(sc) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(sc, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Arrows Box
        GlassSurface(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSendKey("", 0x25) }) { // Left
                    Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onSendKey("", 0x26) }) { // Up
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = Color.White)
                    }
                    IconButton(onClick = { onSendKey("", 0x28) }) { // Down
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = Color.White)
                    }
                }
                IconButton(onClick = { onSendKey("", 0x27) }) { // Right
                    Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = Color.White)
                }
            }
        }
    }
}
