package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.model.DiscoveredHost
import com.knowthemice.app.ui.components.GlassSurface
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
    onNavigate: (String) -> Unit
) {
    var isTouching by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(16.dp)
    ) {
        // Top Compact Status Pill
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
                        .background(StatusSuccess)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentHost?.name ?: "Connected PC",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${latencyMs}ms",
                    color = BrandHighlight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onOpenPower, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power", tint = BrandOrange)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Giant Interactive Touchpad Surface
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onMouseClick("LEFT", "CLICK")
                        },
                        onDoubleTap = {
                            onMouseClick("DOUBLE", "CLICK")
                        },
                        onLongPress = {
                            onMouseClick("RIGHT", "CLICK")
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isTouching = true },
                        onDragEnd = { isTouching = false },
                        onDragCancel = { isTouching = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onMouseMove(dragAmount.x, dragAmount.y)
                        }
                    )
                },
            borderBrush = androidx.compose.ui.graphics.SolidColor(
                if (isTouching) BrandHighlight else GlassBorder
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAirMouseActive) "AIR MOUSE ACTIVE (GYRO)" else "TOUCHPAD SURFACE",
                    color = if (isAirMouseActive) BrandHighlight else Color(0x30FFFFFF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp
                )

                // Scroll strip indicator on right edge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(36.dp)
                        .fillMaxHeight()
                        .padding(vertical = 24.dp, horizontal = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x08FFFFFF))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onMouseScroll(0f, dragAmount.y * 0.1f)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(width = 4.dp, height = 32.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x40FFA550))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mouse Button Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onMouseClick("LEFT", "CLICK") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GlassBorder)),
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
            ) {
                Text("LEFT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { onMouseClick("MIDDLE", "CLICK") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x10FFFFFF)),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GlassBorder)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text("MID", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = { onMouseClick("RIGHT", "CLICK") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GlassBorder)),
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
            ) {
                Text("RIGHT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Floating Quick Tools Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onToggleAirMouse,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isAirMouseActive) BrandHighlight else Color(0x15FFFFFF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Air Mouse", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = { onNavigate("keyboard") },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0x15FFFFFF), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Keys", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = { onNavigate("media") },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0x15FFFFFF), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Media", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = { onNavigate("presentation") },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0x15FFFFFF), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Slideshow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Slides", fontSize = 11.sp)
            }
        }
    }
}
