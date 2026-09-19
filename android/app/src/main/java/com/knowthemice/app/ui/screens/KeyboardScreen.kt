package com.knowthemice.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.ui.theme.*

@Composable
fun KeyboardScreen(
    onSendKey: (key: String, code: Int, action: String) -> Unit,
    onSendTextInput: (text: String) -> Unit,
    onSendShortcut: (name: String) -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    onBack: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("QWERTY") } // "QWERTY", "FN_NAV", "SHORTCUTS"

    // Modifier states
    var isCtrlActive by remember { mutableStateOf(false) }
    var isAltActive by remember { mutableStateOf(false) }
    var isShiftActive by remember { mutableStateOf(false) }
    var isWinActive by remember { mutableStateOf(false) }
    var isCapsLockActive by remember { mutableStateOf(false) }

    fun handleKeyClick(key: KeyItem) {
        when {
            key.isModifier -> {
                when (key.vk) {
                    WindowsVirtualKeys.VK_CONTROL -> {
                        isCtrlActive = !isCtrlActive
                        onSendKey("", key.vk, if (isCtrlActive) "DOWN" else "UP")
                    }
                    WindowsVirtualKeys.VK_MENU -> {
                        isAltActive = !isAltActive
                        onSendKey("", key.vk, if (isAltActive) "DOWN" else "UP")
                    }
                    WindowsVirtualKeys.VK_SHIFT -> {
                        isShiftActive = !isShiftActive
                        onSendKey("", key.vk, if (isShiftActive) "DOWN" else "UP")
                    }
                    WindowsVirtualKeys.VK_LWIN -> {
                        isWinActive = !isWinActive
                        onSendKey("", key.vk, if (isWinActive) "DOWN" else "UP")
                    }
                    WindowsVirtualKeys.VK_CAPITAL -> {
                        isCapsLockActive = !isCapsLockActive
                        onSendKey("", key.vk, "PRESS")
                    }
                }
            }
            else -> {
                if (key.vk > 0) {
                    onSendKey(key.char.ifEmpty { key.label }, key.vk, "PRESS")
                } else if (key.char.isNotEmpty()) {
                    onSendTextInput(key.char)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "WIRELESS KEYBOARD",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleHaptics,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (hapticsEnabled) Icons.Default.Vibration else Icons.Default.VolumeOff,
                        contentDescription = "Haptics",
                        tint = if (hapticsEnabled) BrandHighlight else TextMuted
                    )
                }
            }
        }

        // Live Text Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Type text / paste unicode here...", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandHighlight,
                    unfocusedBorderColor = Color(0x30FFFFFF),
                    focusedContainerColor = Color(0x0AFFFFFF),
                    unfocusedContainerColor = Color(0x05FFFFFF)
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        onSendTextInput(textInput)
                        textInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text("SEND", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            }
        }

        // Mode Switcher Tabs (QWERTY / FN & NAV / SHORTCUTS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "QWERTY" to "Full QWERTY",
                "FN_NAV" to "F1-F12 & Nav",
                "SHORTCUTS" to "PC Shortcuts"
            ).forEach { (mode, title) ->
                val isSel = selectedTab == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0x28FF8A00) else Color(0x0AFFFFFF))
                        .border(
                            width = 1.dp,
                            color = if (isSel) BrandHighlight else Color(0x18FFFFFF),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = mode }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSel) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Active Modifiers Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("CTRL", isCtrlActive, WindowsVirtualKeys.VK_CONTROL),
                Triple("ALT", isAltActive, WindowsVirtualKeys.VK_MENU),
                Triple("SHIFT", isShiftActive, WindowsVirtualKeys.VK_SHIFT),
                Triple("WIN", isWinActive, WindowsVirtualKeys.VK_LWIN),
                Triple("CAPS", isCapsLockActive, WindowsVirtualKeys.VK_CAPITAL)
            ).forEach { (label, active, vk) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) BrandHighlight else Color(0x10FFFFFF))
                        .border(
                            width = 1.dp,
                            color = if (active) BrandAmber else Color(0x15FFFFFF),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            when (vk) {
                                WindowsVirtualKeys.VK_CONTROL -> {
                                    isCtrlActive = !isCtrlActive
                                    onSendKey("", vk, if (isCtrlActive) "DOWN" else "UP")
                                }
                                WindowsVirtualKeys.VK_MENU -> {
                                    isAltActive = !isAltActive
                                    onSendKey("", vk, if (isAltActive) "DOWN" else "UP")
                                }
                                WindowsVirtualKeys.VK_SHIFT -> {
                                    isShiftActive = !isShiftActive
                                    onSendKey("", vk, if (isShiftActive) "DOWN" else "UP")
                                }
                                WindowsVirtualKeys.VK_LWIN -> {
                                    isWinActive = !isWinActive
                                    onSendKey("", vk, if (isWinActive) "DOWN" else "UP")
                                }
                                WindowsVirtualKeys.VK_CAPITAL -> {
                                    isCapsLockActive = !isCapsLockActive
                                    onSendKey("", vk, "PRESS")
                                }
                            }
                        }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (active) Color.Black else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Main Keyboard Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                "QWERTY" -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        KeyboardRow(items = KeyboardLayouts.ROW_NUMBERS, isShift = isShiftActive, isCaps = isCapsLockActive, onKey = ::handleKeyClick)
                        KeyboardRow(items = KeyboardLayouts.ROW_QWERTY, isShift = isShiftActive, isCaps = isCapsLockActive, onKey = ::handleKeyClick)
                        KeyboardRow(items = KeyboardLayouts.ROW_ASDF, isShift = isShiftActive, isCaps = isCapsLockActive, onKey = ::handleKeyClick)
                        KeyboardRow(items = KeyboardLayouts.ROW_ZXCV, isShift = isShiftActive, isCaps = isCapsLockActive, onKey = ::handleKeyClick)
                        KeyboardRow(items = KeyboardLayouts.ROW_BOTTOM, isShift = isShiftActive, isCaps = isCapsLockActive, onKey = ::handleKeyClick)
                    }
                }

                "FN_NAV" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("FUNCTION KEYS (F1 - F12)", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            // F1 - F6
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (1..6).forEach { i ->
                                    KeyButton(item = KeyItem("F$i", vk = 0x6F + i), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // F7 - F12
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (7..12).forEach { i ->
                                    KeyButton(item = KeyItem("F$i", vk = 0x6F + i), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("NAVIGATION & EDITING CLUSTER", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyButton(item = KeyItem("PRTSC", vk = WindowsVirtualKeys.VK_SNAPSHOT), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                KeyButton(item = KeyItem("INSERT", vk = WindowsVirtualKeys.VK_INSERT), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                KeyButton(item = KeyItem("DELETE", vk = WindowsVirtualKeys.VK_DELETE), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyButton(item = KeyItem("HOME", vk = WindowsVirtualKeys.VK_HOME), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                KeyButton(item = KeyItem("PG UP", vk = WindowsVirtualKeys.VK_PRIOR), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                KeyButton(item = KeyItem("PG DN", vk = WindowsVirtualKeys.VK_NEXT), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                                KeyButton(item = KeyItem("END", vk = WindowsVirtualKeys.VK_END), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.weight(1f))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("DIRECTIONAL PAD", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                KeyButton(item = KeyItem("▲ UP", vk = WindowsVirtualKeys.VK_UP), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.width(120.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                KeyButton(item = KeyItem("◀ LEFT", vk = WindowsVirtualKeys.VK_LEFT), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.width(100.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                KeyButton(item = KeyItem("▼ DOWN", vk = WindowsVirtualKeys.VK_DOWN), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.width(100.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                KeyButton(item = KeyItem("RIGHT ▶", vk = WindowsVirtualKeys.VK_RIGHT), isShift = false, isCaps = false, onClick = ::handleKeyClick, modifier = Modifier.width(100.dp))
                            }
                        }
                    }
                }

                "SHORTCUTS" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("STANDARD CLIPBOARD & EDIT", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ShortcutCard("Ctrl + C", "Copy", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Ctrl + V", "Paste", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Ctrl + X", "Cut", onSendShortcut, Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ShortcutCard("Ctrl + Z", "Undo", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Ctrl + Y", "Redo", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Ctrl + A", "Select All", onSendShortcut, Modifier.weight(1f))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("WINDOWS WORKSPACE SHORTCUTS", color = BrandHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ShortcutCard("Alt + Tab", "Switch App", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Alt + F4", "Close App", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Win + D", "Desktop", onSendShortcut, Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ShortcutCard("Win + E", "Explorer", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Win + L", "Lock PC", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Win + R", "Run...", onSendShortcut, Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ShortcutCard("Ctrl+Shift+Esc", "Task Mgr", onSendShortcut, Modifier.weight(1f))
                                ShortcutCard("Win + Print", "Screenshot", onSendShortcut, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyboardRow(
    items: List<KeyItem>,
    isShift: Boolean,
    isCaps: Boolean,
    onKey: (KeyItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            KeyButton(
                item = item,
                isShift = isShift,
                isCaps = isCaps,
                onClick = onKey,
                modifier = Modifier.weight(item.weight)
            )
        }
    }
}

@Composable
fun KeyButton(
    item: KeyItem,
    isShift: Boolean,
    isCaps: Boolean,
    onClick: (KeyItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Determine uppercase / shift character display
    val displayLabel = remember(item.label, isShift, isCaps) {
        if (item.label.length == 1 && item.label[0].isLetter()) {
            if (isShift xor isCaps) item.label.uppercase() else item.label.lowercase()
        } else if (isShift && item.subLabel.isNotEmpty()) {
            item.subLabel
        } else {
            item.label
        }
    }

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(
                when {
                    isPressed -> BrandHighlight
                    item.isAction -> Color(0x28FFFFFF)
                    item.isModifier -> Color(0x1CFFFFFF)
                    else -> Color(0x10FFFFFF)
                }
            )
            .border(
                width = 1.dp,
                color = when {
                    isPressed -> BrandAmber
                    item.isAction -> Color(0x40FFFFFF)
                    else -> Color(0x15FFFFFF)
                },
                shape = RoundedCornerShape(7.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick(item)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (item.subLabel.isNotEmpty() && !isShift) {
                Text(
                    text = item.subLabel,
                    color = TextMuted,
                    fontSize = 8.sp,
                    lineHeight = 9.sp
                )
            }
            Text(
                text = displayLabel,
                color = if (isPressed) Color.Black else Color.White,
                fontSize = if (displayLabel.length > 3) 9.sp else 12.sp,
                fontWeight = if (item.isAction || item.isModifier) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ShortcutCard(
    combo: String,
    title: String,
    onTrigger: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x10FFFFFF))
            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(8.dp))
            .clickable { onTrigger(combo) }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(combo, color = BrandHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
