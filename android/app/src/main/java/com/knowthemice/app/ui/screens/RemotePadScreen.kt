package com.knowthemice.app.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.ui.theme.*

@Composable
fun RemotePadScreen(
    currentHost: DiscoveredHost?,
    latencyMs: Long,
    isAirMouseActive: Boolean,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onMouseClick: (button: String, action: String) -> Unit,
    onMouseScroll: (dx: Float, dy: Float) -> Unit,
    onToggleAirMouse: () -> Unit,
    onOpenPower: () -> Unit,
    onDisconnect: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    var isTouching by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    var isDraggingLock by remember { mutableStateOf(false) }
    var isLeftPressed by remember { mutableStateOf(false) }
    var isRightPressed by remember { mutableStateOf(false) }
    var isMidPressed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Top Modern Header Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1218))
                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connected Host Pill
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
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentHost?.name ?: "Connected PC",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentHost?.ip ?: "127.0.0.1",
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Badges & Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Latency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x18FF5E00))
                        .border(1.dp, Color(0x40FF5E00), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${latencyMs}ms",
                        color = BrandHighlight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Power Controls
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x20FF5E00))
                        .clickable(onClick = onOpenPower),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = BrandOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Disconnect
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x15FFFFFF))
                        .clickable(onClick = onDisconnect),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Disconnect",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Giant Ergonomic Touchpad Area with Scroll Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1017),
                            Color(0xFF07090D)
                        )
                    )
                )
                .border(
                    1.5.dp,
                    if (isDraggingLock) SolidColor(StatusWarning)
                    else if (isTouching) SolidColor(BrandHighlight)
                    else SolidColor(Color(0x25FF5E00)),
                    RoundedCornerShape(20.dp)
                )
        ) {
            // Touchpad Main Surface Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 46.dp) // Leave room for dedicated scroll strip
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                touchPosition = offset
                                onMouseClick("LEFT", "CLICK")
                            },
                            onDoubleTap = { offset ->
                                touchPosition = offset
                                onMouseClick("DOUBLE", "CLICK")
                            },
                            onLongPress = { offset ->
                                touchPosition = offset
                                isDraggingLock = true
                                onMouseClick("LEFT", "DOWN")
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isTouching = true
                                touchPosition = offset
                            },
                            onDragEnd = {
                                isTouching = false
                                touchPosition = null
                                if (isDraggingLock) {
                                    isDraggingLock = false
                                    onMouseClick("LEFT", "UP")
                                }
                            },
                            onDragCancel = {
                                isTouching = false
                                touchPosition = null
                                if (isDraggingLock) {
                                    isDraggingLock = false
                                    onMouseClick("LEFT", "UP")
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                touchPosition = change.position
                                onMouseMove(dragAmount.x, dragAmount.y)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Subtle Center Branding / Status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isAirMouseActive) "AIR MOUSE ACTIVE (GYRO)"
                        else if (isDraggingLock) "DRAG LOCK ACTIVE"
                        else "PRECISION TRACKPAD",
                        color = if (isAirMouseActive || isDraggingLock) BrandHighlight else Color(0x35FFFFFF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1-Tap: Left · 2-Tap: Right · Hold: Drag",
                        color = Color(0x20FFFFFF),
                        fontSize = 10.sp
                    )
                }

                // Dynamic Touch Indicator
                if (isTouching && touchPosition != null) {
                    touchPosition?.let { pos ->
                        Box(
                            modifier = Modifier
                                .offset(x = (pos.x - 24).dp, y = (pos.y - 24).dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x18FF5E00))
                                .border(1.5.dp, Color(0x60FF5E00), CircleShape)
                        )
                    }
                }
            }

            // Dedicated Ergonomic Vertical Scroll Strip on Right Margin
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(46.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                    .background(Color(0xFF0B0D12))
                    .border(
                        1.dp,
                        Color(0x15FFFFFF),
                        RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Smooth scroll events
                            onMouseScroll(0f, dragAmount.y * 0.12f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 18.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll Up",
                        tint = Color(0x50FFA550),
                        modifier = Modifier.size(18.dp)
                    )

                    // Scroll Handle Pill
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(BrandHighlight, BrandOrange)
                                )
                            )
                    )

                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll Down",
                        tint = Color(0x50FFA550),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tactile Ergonomic Click Bar (Left, Mid, Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // LEFT CLICK BUTTON (Large tactile area)
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isLeftPressed) Color(0x35FF5E00) else Color(0xFF131720)
                    )
                    .border(
                        1.5.dp,
                        if (isLeftPressed) BrandHighlight else Color(0x30FF5E00),
                        RoundedCornerShape(16.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = if (isLeftPressed) BrandHighlight else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LEFT CLICK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // MIDDLE CLICK BUTTON
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isMidPressed) Color(0x25FF5E00) else Color(0xFF10131A)
                    )
                    .border(
                        1.dp,
                        if (isMidPressed) BrandHighlight else Color(0x20FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isMidPressed = true
                                onMouseClick("MIDDLE", "CLICK")
                                tryAwaitRelease()
                                isMidPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MID",
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // RIGHT CLICK BUTTON
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isRightPressed) Color(0x35FF5E00) else Color(0xFF131720)
                    )
                    .border(
                        1.5.dp,
                        if (isRightPressed) BrandHighlight else Color(0x30FF5E00),
                        RoundedCornerShape(16.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RIGHT CLICK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Floating Glass Mode Dock with Overlapping/Prominent Mouse Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1218))
                .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(18.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home (Return to Home Screen)
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Home,
                label = "Home",
                isActive = false,
                onClick = { onNavigate("home") }
            )

            // Trackpad Mode (Active prominent mouse)
            DockItem(
                modifier = Modifier.weight(1.05f),
                icon = Icons.Default.Mouse,
                label = "Mouse",
                isActive = !isAirMouseActive,
                isProminent = true,
                onClick = { if (isAirMouseActive) onToggleAirMouse() }
            )

            // Air Mouse (Gyro)
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Navigation,
                label = "Air Mouse",
                isActive = isAirMouseActive,
                onClick = onToggleAirMouse
            )

            // Keyboard
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Keyboard,
                label = "Keys",
                isActive = false,
                onClick = { onNavigate("keyboard") }
            )

            // Media
            DockItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PlayArrow,
                label = "Media",
                isActive = false,
                onClick = { onNavigate("media") }
            )
        }
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
