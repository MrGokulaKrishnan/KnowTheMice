package com.knowthemice.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.ui.theme.*

enum class InputMode(
    val title: String,
    val type: KeyboardType,
    val imeAction: ImeAction
) {
    TEXT("Text", KeyboardType.Text, ImeAction.Send),
    MULTILINE("Multiline", KeyboardType.Text, ImeAction.Default),
    NUMBER("Number", KeyboardType.Number, ImeAction.Send),
    URL("URL", KeyboardType.Uri, ImeAction.Go),
    EMAIL("Email", KeyboardType.Email, ImeAction.Send),
    PASSWORD("Password", KeyboardType.Password, ImeAction.Done)
}

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
    var selectedMode by remember { mutableStateOf(InputMode.TEXT) }
    var isLiveStreamEnabled by remember { mutableStateOf(true) }
    var showShortcutsPanel by remember { mutableStateOf(false) }
    var showFnPanel by remember { mutableStateOf(false) }

    // Modifier states (sticky toggleable)
    var isCtrlActive by remember { mutableStateOf(false) }
    var isAltActive by remember { mutableStateOf(false) }
    var isShiftActive by remember { mutableStateOf(false) }
    var isWinActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current

    // Automatically request focus and open native IME on mount
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    fun handleSendCurrentBuffer() {
        if (textInput.isNotEmpty()) {
            onSendTextInput(textInput)
            textInput = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding() // CRITICAL: Automatically keeps PC toolbar and active fields docked above native Gboard/Samsung Keyboard
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // 1. Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NATIVE KEYBOARD",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "GBOARD • SAMSUNG • SYSTEM IME",
                    color = BrandHighlight,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Show Keyboard",
                        tint = BrandHighlight
                    )
                }

                IconButton(
                    onClick = onToggleHaptics,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (hapticsEnabled) Icons.Default.Vibration else Icons.Default.VolumeOff,
                        contentDescription = "Haptics",
                        tint = if (hapticsEnabled) BrandHighlight else TextMuted
                    )
                }
            }
        }

        // 2. Keyboard Type Selector Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(InputMode.values()) { mode ->
                val isSelected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0x28FF8A00) else Color(0x0AFFFFFF))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BrandHighlight else Color(0x18FFFFFF),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            selectedMode = mode
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 3. Live Native Text Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x08FFFFFF))
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(10.dp)
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
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isLiveStreamEnabled) StatusSuccess else BrandAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveStreamEnabled) "Live Keystroke Stream" else "Buffered Send Mode",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isLiveStreamEnabled) "SWITCH TO BUFFER" else "SWITCH TO LIVE",
                            color = BrandHighlight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isLiveStreamEnabled = !isLiveStreamEnabled }
                        )

                        if (textInput.isNotEmpty()) {
                            Text(
                                text = "CLEAR",
                                color = StatusError,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { textInput = "" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { newText ->
                        if (isLiveStreamEnabled) {
                            if (newText.length > textInput.length) {
                                val addedChars = newText.substring(textInput.length)
                                onSendTextInput(addedChars)
                            } else if (newText.length < textInput.length) {
                                val deletedCount = textInput.length - newText.length
                                repeat(deletedCount) {
                                    onSendKey("", WindowsVirtualKeys.VK_BACK, "PRESS")
                                }
                            }
                        }
                        textInput = newText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            text = when (selectedMode) {
                                InputMode.TEXT -> "Type with Gboard / use voice dictation..."
                                InputMode.MULTILINE -> "Type multi-line message or code..."
                                InputMode.NUMBER -> "Enter numbers or calculations..."
                                InputMode.URL -> "Enter website URL or IP..."
                                InputMode.EMAIL -> "Enter email address..."
                                InputMode.PASSWORD -> "Enter password securely..."
                            },
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = selectedMode != InputMode.MULTILINE,
                    maxLines = if (selectedMode == InputMode.MULTILINE) 4 else 1,
                    visualTransformation = if (selectedMode == InputMode.PASSWORD) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = selectedMode.type,
                        imeAction = selectedMode.imeAction,
                        autoCorrect = selectedMode == InputMode.TEXT || selectedMode == InputMode.MULTILINE
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (isLiveStreamEnabled) {
                                onSendKey("", WindowsVirtualKeys.VK_RETURN, "PRESS")
                            } else {
                                handleSendCurrentBuffer()
                            }
                        },
                        onDone = {
                            if (isLiveStreamEnabled) {
                                onSendKey("", WindowsVirtualKeys.VK_RETURN, "PRESS")
                            } else {
                                handleSendCurrentBuffer()
                            }
                            keyboardController?.hide()
                        },
                        onGo = {
                            if (isLiveStreamEnabled) {
                                onSendKey("", WindowsVirtualKeys.VK_RETURN, "PRESS")
                            } else {
                                handleSendCurrentBuffer()
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BrandHighlight,
                        unfocusedBorderColor = Color(0x30FFFFFF),
                        focusedContainerColor = Color(0x05FFFFFF),
                        unfocusedContainerColor = Color(0x02FFFFFF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Quick Send / Paste Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Paste from Android Clipboard directly to PC
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x14FFFFFF))
                                .clickable {
                                    val clipText = clipboardManager.getText()?.text
                                    if (!clipText.isNullOrEmpty()) {
                                        onSendTextInput(clipText)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Paste to PC", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        // Voice Hint
                        Text(
                            text = "Tip: Tap 🎙 on your keyboard for Voice Typing",
                            color = TextMuted,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    if (!isLiveStreamEnabled) {
                        Button(
                            onClick = ::handleSendCurrentBuffer,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("SEND TO PC", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Compact PC Modifiers & Control Bar (Always visible above Gboard)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Modifiers Row: CTRL, ALT, SHIFT, WIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ModifierKeyButton(
                    label = "CTRL",
                    isActive = isCtrlActive,
                    onClick = {
                        isCtrlActive = !isCtrlActive
                        onSendKey("", WindowsVirtualKeys.VK_CONTROL, if (isCtrlActive) "DOWN" else "UP")
                    },
                    modifier = Modifier.weight(1f)
                )

                ModifierKeyButton(
                    label = "ALT",
                    isActive = isAltActive,
                    onClick = {
                        isAltActive = !isAltActive
                        onSendKey("", WindowsVirtualKeys.VK_MENU, if (isAltActive) "DOWN" else "UP")
                    },
                    modifier = Modifier.weight(1f)
                )

                ModifierKeyButton(
                    label = "SHIFT",
                    isActive = isShiftActive,
                    onClick = {
                        isShiftActive = !isShiftActive
                        onSendKey("", WindowsVirtualKeys.VK_SHIFT, if (isShiftActive) "DOWN" else "UP")
                    },
                    modifier = Modifier.weight(1f)
                )

                ModifierKeyButton(
                    label = "WIN",
                    isActive = isWinActive,
                    onClick = {
                        isWinActive = !isWinActive
                        onSendKey("", WindowsVirtualKeys.VK_LWIN, if (isWinActive) "DOWN" else "UP")
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Essential PC Action Keys: ESC, TAB, ENTER, BACKSPACE, DELETE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickActionButton(
                    label = "ESC",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_ESCAPE, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "TAB",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_TAB, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "BKSP",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_BACK, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "DEL",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_DELETE, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "ENTER",
                    isPrimary = true,
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_RETURN, "PRESS") },
                    modifier = Modifier.weight(1.3f)
                )
            }

            // Arrow Direction Keys Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionButton(
                    label = "◀ LEFT",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_LEFT, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "▲ UP",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_UP, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "▼ DOWN",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_DOWN, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "RIGHT ▶",
                    onClick = { onSendKey("", WindowsVirtualKeys.VK_RIGHT, "PRESS") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Toolbars Toggles: F1-F12 & Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showFnPanel) Color(0x30FF8A00) else Color(0x10FFFFFF))
                        .border(1.dp, if (showFnPanel) BrandHighlight else Color(0x18FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { showFnPanel = !showFnPanel }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showFnPanel) "Hide F1-F12" else "F1-F12 & Nav",
                        color = if (showFnPanel) BrandHighlight else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showShortcutsPanel) Color(0x30FF8A00) else Color(0x10FFFFFF))
                        .border(1.dp, if (showShortcutsPanel) BrandHighlight else Color(0x18FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { showShortcutsPanel = !showShortcutsPanel }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showShortcutsPanel) "Hide Shortcuts" else "PC Shortcuts",
                        color = if (showShortcutsPanel) BrandHighlight else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Collapsible Function Keys (F1 - F12 & Nav)
            AnimatedVisibility(visible = showFnPanel) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x0CFFFFFF))
                        .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("FUNCTION KEYS (F1 - F12)", color = BrandHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    // F1 - F6
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..6).forEach { i ->
                            QuickActionButton(
                                label = "F$i",
                                onClick = { onSendKey("", 0x6F + i, "PRESS") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    // F7 - F12
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (7..12).forEach { i ->
                            QuickActionButton(
                                label = "F$i",
                                onClick = { onSendKey("", 0x6F + i, "PRESS") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("NAVIGATION & EDITING", color = BrandHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        QuickActionButton(label = "PRTSC", onClick = { onSendKey("", WindowsVirtualKeys.VK_SNAPSHOT, "PRESS") }, modifier = Modifier.weight(1f))
                        QuickActionButton(label = "HOME", onClick = { onSendKey("", WindowsVirtualKeys.VK_HOME, "PRESS") }, modifier = Modifier.weight(1f))
                        QuickActionButton(label = "END", onClick = { onSendKey("", WindowsVirtualKeys.VK_END, "PRESS") }, modifier = Modifier.weight(1f))
                        QuickActionButton(label = "PG UP", onClick = { onSendKey("", WindowsVirtualKeys.VK_PRIOR, "PRESS") }, modifier = Modifier.weight(1f))
                        QuickActionButton(label = "PG DN", onClick = { onSendKey("", WindowsVirtualKeys.VK_NEXT, "PRESS") }, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Collapsible PC Shortcuts Panel
            AnimatedVisibility(visible = showShortcutsPanel) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x0CFFFFFF))
                        .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("STANDARD CLIPBOARD SHORTCUTS", color = BrandHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShortcutChip("Ctrl + C", "Copy", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Ctrl + V", "Paste", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Ctrl + X", "Cut", onSendShortcut, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShortcutChip("Ctrl + Z", "Undo", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Ctrl + Y", "Redo", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Ctrl + A", "Select All", onSendShortcut, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("SYSTEM & WINDOWS SHORTCUTS", color = BrandHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShortcutChip("Alt + Tab", "Switch", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Alt + F4", "Close", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Win + D", "Desktop", onSendShortcut, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShortcutChip("Win + L", "Lock PC", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Win + E", "Explorer", onSendShortcut, Modifier.weight(1f))
                        ShortcutChip("Ctrl+Shift+Esc", "Task Mgr", onSendShortcut, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ModifierKeyButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) BrandHighlight else Color(0x14FFFFFF))
            .border(
                width = 1.dp,
                color = if (isActive) BrandAmber else Color(0x20FFFFFF),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) Color.Black else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (isPrimary) Color(0x35FF8A00) else Color(0x10FFFFFF))
            .border(
                width = 1.dp,
                color = if (isPrimary) BrandHighlight else Color(0x18FFFFFF),
                shape = RoundedCornerShape(7.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPrimary) BrandHighlight else Color.White,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ShortcutChip(
    combo: String,
    title: String,
    onTrigger: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x12FFFFFF))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(6.dp))
            .clickable { onTrigger(combo) }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(combo, color = BrandHighlight, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text(title, color = TextSecondary, fontSize = 8.sp)
        }
    }
}
