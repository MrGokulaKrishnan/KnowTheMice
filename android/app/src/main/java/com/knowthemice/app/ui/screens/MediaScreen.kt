package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.theme.*

@Composable
fun MediaScreen(
    onMediaCommand: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedProfile by remember { mutableStateOf("Spotify") }
    var isPlaying by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            Text("MEDIA CONTROLLER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Profiles Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Spotify", "YouTube", "Netflix", "VLC").forEach { profile ->
                val isSelected = selectedProfile == profile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) BrandHighlight else Color(0x15FFFFFF))
                        .clickable { selectedProfile = profile }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Large Circular Play/Pause Hub
        GlassSurface(
            modifier = Modifier.size(240.dp),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onMediaCommand("PREV") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(BrandGradient)
                            .clickable {
                                isPlaying = !isPlaying
                                onMediaCommand("PLAY_PAUSE")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = { onMediaCommand("NEXT") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Volume Controls
        GlassSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMediaCommand("MUTE") }) {
                    Icon(Icons.Default.VolumeOff, contentDescription = "Mute", tint = BrandOrange)
                }

                Button(
                    onClick = { onMediaCommand("VOL_DOWN") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VolumeDown, contentDescription = "Vol Down", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("VOL -", fontSize = 12.sp)
                }

                Button(
                    onClick = { onMediaCommand("VOL_UP") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Vol Up", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("VOL +", fontSize = 12.sp)
                }
            }
        }
    }
}
