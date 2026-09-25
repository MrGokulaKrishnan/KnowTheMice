package com.knowthemice.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.model.ControlMode
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.ui.theme.*

@Composable
fun RemoteControlScreen(
    currentHost: DiscoveredHost?,
    latencyMs: Long,
    initialMode: ControlMode = ControlMode.MOUSE,
    isAirMouseActive: Boolean,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onMouseClick: (button: String, action: String) -> Unit,
    onMouseScroll: (dx: Float, dy: Float) -> Unit,
    onToggleAirMouse: () -> Unit,
    onSendKey: (key: String, code: Int, action: String) -> Unit,
    onSendTextInput: (text: String) -> Unit,
    onSendShortcut: (name: String) -> Unit,
    onReleaseAllKeys: () -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    onOpenPower: () -> Unit,
    onDisconnect: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    var controlMode by remember { mutableStateOf(initialMode) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Mouse Touchpad state
    var isTouching by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    var isDraggingLock by remember { mutableStateOf(false) }
    var isLeftPressed by remember { mutableStateOf(false) }
    var isRightPressed by remember { mutableStateOf(false) }
    var isMidPressed by remember { mutableStateOf(false) }

    // Keyboard state
    var textInput by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(InputMode.TEXT) }
    var isLiveStreamEnabled by remember { mutableStateOf(true) }
    var showShortcutsPanel by remember { mutableStateOf(false) }
    var showFnPanel by remember { mutableStateOf(false) }

    // Sticky modifier states
    var isCtrlActive by remember { mutableStateOf(false) }
    var isAltActive by remember { mutableStateOf(false) }
    var isShiftActive by remember { mutableStateOf(false) }
    var isWinActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Safe mode switcher helper
    fun switchMode(targetMode: ControlMode) {
        if (targetMode == controlMode) return
        if (targetMode == ControlMode.MOUSE) {
            // Smoothly hide software keyboard and release all modifiers on PC
            keyboardController?.hide()
            isCtrlActive = false
            isAltActive = false
            isShiftActive = false
            isWinActive = false
            onReleaseAllKeys()
        }
        controlMode = targetMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // 1. Top Modern Header Bar with PC Status & Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1218))
                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(18.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host Pill
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = currentHost?.name ?: "Connected PC",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${latencyMs}ms • ${currentHost?.ip ?: "127.0.0.1"}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Apple-Style Liquid Glass Mode Switcher Pill [ 🖱 Mouse | ⌨ Keyboard ]
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x18FFFFFF))
                    .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(20.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mouse Mode Pill
                val mouseModifier = if (controlMode == ControlMode.MOUSE) {
                    Modifier.background(Brush.horizontalGradient(listOf(BrandOrange, BrandHighlight)))
                } else {
                    Modifier.background(Color.Transparent)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then(mouseModifier)
                        .clickable { switchMode(ControlMode.MOUSE) }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Mouse,
                            contentDescription = "Mouse",
                            tint = if (controlMode == ControlMode.MOUSE) Color.Black else Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mouse",
                            color = if (controlMode == ControlMode.MOUSE) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Keyboard Mode Pill
                val kbModifier = if (controlMode == ControlMode.KEYBOARD) {
                    Modifier.background(Brush.horizontalGradient(listOf(BrandOrange, BrandHighlight)))
                } else {
                    Modifier.background(Color.Transparent)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then(kbModifier)
                        .clickable { switchMode(ControlMode.KEYBOARD) }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Keyboard,
                            contentDescription = "Keyboard",
                            tint = if (controlMode == ControlMode.KEYBOARD) Color.Black else Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Keyboard",
                            color = if (controlMode == ControlMode.KEYBOARD) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Buttons (Power & Disconnect)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x20FF5E00))
                        .clickable(onClick = onOpenPower),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = BrandOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x15FFFFFF))
                        .clickable(onClick = onDisconnect),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Disconnect",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Main Content Area (Mouse or Keyboard)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (controlMode == ControlMode.MOUSE) {
                // ============================================
                // MOUSE / TRACKPAD MODE VIEW
                // ============================================
                Column(modifier = Modifier.fillMaxSize()) {
                    // Giant Ergonomic Touchpad Area with Scroll Strip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF141720), Color(0xFF0A0C10))
                                )
                            )
                            .border(
                                width = if (isTouching) 1.5.dp else 1.dp,
                                brush = if (isTouching) Brush.linearGradient(listOf(BrandHighlight, BrandOrange))
                                else Brush.linearGradient(listOf(Color(0x25FFFFFF), Color(0x10FFFFFF))),
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        // Interactive Touch Zone
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onMouseClick("LEFT", "CLICK")
                                        },
                                        onDoubleTap = {
                                            onMouseClick("LEFT", "CLICK")
                                            onMouseClick("LEFT", "CLICK")
                                        },
                                        onLongPress = {
                                            onMouseClick("RIGHT", "CLICK")
                                        }
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            isTouching = true
                                            touchPosition = offset
                                            if (isDraggingLock) {
                                                onMouseClick("LEFT", "DOWN")
                                            }
                                        },
                                        onDragEnd = {
                                            isTouching = false
                                            touchPosition = null
                                            if (isDraggingLock) {
                                                onMouseClick("LEFT", "UP")
                                                isDraggingLock = false
                                            }
                                        },
                                        onDragCancel = {
                                            isTouching = false
                                            touchPosition = null
                                            if (isDraggingLock) {
                                                onMouseClick("LEFT", "UP")
                                                isDraggingLock = false
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            touchPosition = change.position
                                            onMouseMove(dragAmount.x, dragAmount.y)
                                        }
                                    )
                                }
                        ) {
                            // Touch Glow Indicator
                            if (isTouching && touchPosition != null) {
                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = (touchPosition!!.x / 2.5f).dp,
                                            y = (touchPosition!!.y / 2.5f).dp
                                        )
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x30FF5E00))
                                )
                            }

                            // Minimalist Trackpad Center Guides
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = if (isTouching) BrandHighlight else Color(0x18FFFFFF),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isDraggingLock) "DRAG LOCK ACTIVE" else "ERGONOMIC GLASS TOUCHPAD",
                                    color = if (isDraggingLock) BrandHighlight else Color(0x30FFFFFF),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "Tap = Left Click • 2-Finger / Long Press = Right Click",
                                    color = Color(0x20FFFFFF),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // Ergonomic Dedicated Scroll Strip on Right Edge
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(44.dp)
                                .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                                .background(Color(0x0CFFFFFF))
                                .border(
                                    width = 1.dp,
                                    color = Color(0x18FFFFFF),
                                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        onMouseScroll(0f, dragAmount.y * 1.5f)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Scroll Up",
                                    tint = Color(0x60FFFFFF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "SCROLL",
                                    color = Color(0x40FFFFFF),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Scroll Down",
                                    tint = Color(0x60FFFFFF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Mouse Buttons (Left, Middle / Drag, Right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Click
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isLeftPressed) Color(0x40FF5E00) else Color(0xFF141822)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isLeftPressed) BrandHighlight else Color(0x25FFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isLeftPressed = true
                                            onMouseClick("LEFT", "DOWN")
                                            tryAwaitRelease()
                                            isLeftPressed = false
                                            onMouseClick("LEFT", "UP")
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LEFT CLICK",
                                color = if (isLeftPressed) BrandHighlight else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Middle Click & Drag Lock
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isDraggingLock || isMidPressed) Color(0x40FF8A00) else Color(0xFF10131B)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDraggingLock || isMidPressed) BrandHighlight else Color(0x20FFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            isDraggingLock = !isDraggingLock
                                        },
                                        onLongPress = {
                                            isMidPressed = true
                                            onMouseClick("MIDDLE", "DOWN")
                                            isMidPressed = false
                                            onMouseClick("MIDDLE", "UP")
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isDraggingLock) "LOCKED" else "MIDDLE",
                                    color = if (isDraggingLock || isMidPressed) BrandHighlight else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (isDraggingLock) "DRAG ON" else "TAP LOCK",
                                    color = if (isDraggingLock) BrandHighlight else TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // Right Click
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isRightPressed) Color(0x40FF5E00) else Color(0xFF141822)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isRightPressed) BrandHighlight else Color(0x25FFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isRightPressed = true
                                            onMouseClick("RIGHT", "DOWN")
                                            tryAwaitRelease()
                                            isRightPressed = false
                                            onMouseClick("RIGHT", "UP")
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RIGHT CLICK",
                                color = if (isRightPressed) BrandHighlight else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Tools Row (Air Mouse Toggle & Haptics)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Air Mouse Sensor Toggle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAirMouseActive) Color(0x35FF5E00) else Color(0x0EFFFFFF))
                                .border(1.dp, if (isAirMouseActive) BrandHighlight else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                .clickable(onClick = onToggleAirMouse)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Sensors,
                                    contentDescription = "Air Mouse",
                                    tint = if (isAirMouseActive) BrandHighlight else Color(0x80FFFFFF),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAirMouseActive) "AIR MOUSE ON" else "AIR MOUSE",
                                    color = if (isAirMouseActive) Color.White else Color(0x80FFFFFF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Haptics Toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x0EFFFFFF))
                                .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                .clickable(onClick = onToggleHaptics)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (hapticsEnabled) Icons.Default.Vibration else Icons.Default.VolumeOff,
                                    contentDescription = "Haptics",
                                    tint = if (hapticsEnabled) BrandHighlight else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (hapticsEnabled) "HAPTICS ON" else "MUTED",
                                    color = if (hapticsEnabled) Color.White else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // ============================================
                // KEYBOARD MODE VIEW
                // ============================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Keyboard Type Selector Chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
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
                                    .clickable { selectedMode = mode }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) BrandHighlight else Color(0x90FFFFFF),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Native System IME Input Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textInput,
                            onValueChange = { newVal ->
                                if (isLiveStreamEnabled) {
                                    if (newVal.length > textInput.length) {
                                        val appended = newVal.substring(textInput.length)
                                        onSendTextInput(appended)
                                    } else if (newVal.length < textInput.length) {
                                        val delCount = textInput.length - newVal.length
                                        repeat(delCount) {
                                            onSendKey("Backspace", 0x08, "PRESS")
                                        }
                                    }
                                }
                                textInput = newVal
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(12.dp)),
                            placeholder = {
                                Text(
                                    text = if (isLiveStreamEnabled) "Live typing directly to PC..." else "Type text buffer...",
                                    color = Color(0x50FFFFFF),
                                    fontSize = 13.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF141720),
                                unfocusedContainerColor = Color(0xFF0F1218),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = selectedMode != InputMode.MULTILINE,
                            visualTransformation = if (selectedMode == InputMode.PASSWORD) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = selectedMode.type,
                                imeAction = selectedMode.imeAction
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (!isLiveStreamEnabled && textInput.isNotEmpty()) {
                                        onSendTextInput(textInput)
                                        textInput = ""
                                    }
                                    onSendKey("Enter", 0x0D, "PRESS")
                                },
                                onSend = {
                                    if (!isLiveStreamEnabled && textInput.isNotEmpty()) {
                                        onSendTextInput(textInput)
                                        textInput = ""
                                    }
                                    onSendKey("Enter", 0x0D, "PRESS")
                                },
                                onGo = {
                                    if (!isLiveStreamEnabled && textInput.isNotEmpty()) {
                                        onSendTextInput(textInput)
                                        textInput = ""
                                    }
                                    onSendKey("Enter", 0x0D, "PRESS")
                                }
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Bring Up Keyboard Button
                        IconButton(
                            onClick = {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x20FF5E00))
                                .border(1.dp, Color(0x40FF5E00), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Keyboard, contentDescription = "IME", tint = BrandHighlight)
                        }

                        // Send button for buffered mode
                        if (!isLiveStreamEnabled) {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (textInput.isNotEmpty()) {
                                        onSendTextInput(textInput)
                                        textInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandHighlight)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                            }
                        }
                    }

                    // Sticky Modifier Bar (Ctrl, Alt, Shift, Win)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ControlModifierKeyButton(
                            title = "Ctrl",
                            isActive = isCtrlActive,
                            onClick = {
                                isCtrlActive = !isCtrlActive
                                onSendKey("Control", 0x11, if (isCtrlActive) "DOWN" else "UP")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ControlModifierKeyButton(
                            title = "Alt",
                            isActive = isAltActive,
                            onClick = {
                                isAltActive = !isAltActive
                                onSendKey("Alt", 0x12, if (isAltActive) "DOWN" else "UP")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ControlModifierKeyButton(
                            title = "Shift",
                            isActive = isShiftActive,
                            onClick = {
                                isShiftActive = !isShiftActive
                                onSendKey("Shift", 0x10, if (isShiftActive) "DOWN" else "UP")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ControlModifierKeyButton(
                            title = "Win",
                            isActive = isWinActive,
                            onClick = {
                                isWinActive = !isWinActive
                                onSendKey("Meta", 0x5B, if (isWinActive) "DOWN" else "UP")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // Release All Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x20EF4444))
                                .border(1.dp, Color(0x50EF4444), RoundedCornerShape(10.dp))
                                .clickable {
                                    isCtrlActive = false
                                    isAltActive = false
                                    isShiftActive = false
                                    isWinActive = false
                                    onReleaseAllKeys()
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reset", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    // Quick PC Control Row (Esc, Tab, Enter, Backspace, Delete)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PCKeyButton("Esc", 0x1B, onSendKey, Modifier.weight(1f))
                        PCKeyButton("Tab", 0x09, onSendKey, Modifier.weight(1f))
                        PCKeyButton("Enter", 0x0D, onSendKey, Modifier.weight(1.2f), isProminent = true)
                        PCKeyButton("Bksp", 0x08, onSendKey, Modifier.weight(1.2f))
                        PCKeyButton("Del", 0x2E, onSendKey, Modifier.weight(1f))
                    }

                    // Navigation Arrow Cluster + Special Keys
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PCKeyButton("Home", 0x24, onSendKey, Modifier.weight(1f))
                        PCKeyButton("▲", 0x26, onSendKey, Modifier.weight(1f))
                        PCKeyButton("End", 0x23, onSendKey, Modifier.weight(1f))
                        PCKeyButton("PgUp", 0x21, onSendKey, Modifier.weight(1f))
                        PCKeyButton("PgDn", 0x22, onSendKey, Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PCKeyButton("◄", 0x25, onSendKey, Modifier.weight(1f))
                        PCKeyButton("▼", 0x28, onSendKey, Modifier.weight(1f))
                        PCKeyButton("►", 0x27, onSendKey, Modifier.weight(1f))
                        PCKeyButton("Space", 0x20, onSendKey, Modifier.weight(2f))
                    }

                    // Quick Windows Shortcuts Row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val shortcuts = listOf(
                            "Ctrl + C", "Ctrl + V", "Ctrl + Z", "Ctrl + Y", "Ctrl + A",
                            "Alt + Tab", "Alt + F4", "Win + D", "Win + E", "Win + L",
                            "Ctrl+Shift+Esc", "Win + Print"
                        )
                        items(shortcuts) { sc ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x18FFFFFF))
                                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { onSendShortcut(sc) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = sc,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Function Keys Bar (F1-F12)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items((1..12).toList()) { fNum ->
                            val vkCode = 0x70 + (fNum - 1)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x10FFFFFF))
                                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { onSendKey("F$fNum", vkCode, "PRESS") }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "F$fNum",
                                    color = BrandHighlight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Ergonomic Bottom Navigation Dock (Mouse ↔ Keyboard ↔ Media ↔ Presentation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F1218))
                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mouse (Trackpad) Mode
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Mouse,
                label = "Mouse",
                isActive = controlMode == ControlMode.MOUSE,
                isProminent = controlMode == ControlMode.MOUSE,
                onClick = { switchMode(ControlMode.MOUSE) }
            )

            // Keyboard Mode
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Keyboard,
                label = "Keyboard",
                isActive = controlMode == ControlMode.KEYBOARD,
                isProminent = controlMode == ControlMode.KEYBOARD,
                onClick = { switchMode(ControlMode.KEYBOARD) }
            )

            // Media
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PlayArrow,
                label = "Media",
                isActive = false,
                onClick = { onNavigate("media") }
            )

            // Presentation
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Slideshow,
                label = "Slides",
                isActive = false,
                onClick = { onNavigate("presentation") }
            )
        }
    }
}

@Composable
private fun ControlModifierKeyButton(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Color(0x35FF5E00) else Color(0x14FFFFFF))
            .border(
                1.dp,
                if (isActive) BrandHighlight else Color(0x25FFFFFF),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isActive) BrandHighlight else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PCKeyButton(
    title: String,
    code: Int,
    onSendKey: (key: String, code: Int, action: String) -> Unit,
    modifier: Modifier = Modifier,
    isProminent: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isProminent) Color(0x25FF5E00) else Color(0x10FFFFFF))
            .border(
                1.dp,
                if (isProminent) BrandHighlight else Color(0x20FFFFFF),
                RoundedCornerShape(8.dp)
            )
            .clickable { onSendKey(title, code, "PRESS") }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isProminent) BrandHighlight else Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DockItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    isProminent: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemBg = if (isProminent && isActive) {
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x40FF5E00), Color(0x20FF8A00))
                )
            )
            .border(1.5.dp, BrandHighlight, RoundedCornerShape(14.dp))
    } else if (isActive) {
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x25FF5E00))
            .border(1.dp, Color(0x40FF5E00), RoundedCornerShape(12.dp))
    } else {
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
    }

    Box(
        modifier = modifier
            .then(itemBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) BrandHighlight else Color(0x80FFFFFF),
                modifier = Modifier.size(if (isProminent) 20.dp else 18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isActive) Color.White else Color(0x60FFFFFF),
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                maxLines = 1,
                softWrap = false,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
