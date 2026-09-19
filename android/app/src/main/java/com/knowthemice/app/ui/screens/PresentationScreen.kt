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
import kotlinx.coroutines.delay

@Composable
fun PresentationScreen(
    onPresentationCommand: (String) -> Unit,
    onBack: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .padding(16.dp),
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
            Text("PRESENTATION DECK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            IconButton(onClick = {
                isTimerRunning = !isTimerRunning
            }) {
                Icon(
                    if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Timer Toggle",
                    tint = BrandHighlight
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Presenter Stopwatch
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
                Column {
                    Text("ELAPSED TIME", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text(timeFormatted, fontSize = 28.sp, color = BrandHighlight, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isTimerRunning = false
                            elapsedSeconds = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { onPresentationCommand("START") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start F5", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Massive Next Slide Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .clip(RoundedCornerShape(24.dp))
                .background(BrandGradient)
                .clickable { onPresentationCommand("NEXT_SLIDE") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("NEXT SLIDE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Previous Slide Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x15FFFFFF))
                .clickable { onPresentationCommand("PREV_SLIDE") },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("PREVIOUS SLIDE", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom Tools: Black Screen & Exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onPresentationCommand("BLACK_SCREEN") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Black Screen (B)", fontSize = 11.sp, color = TextSecondary)
            }

            Button(
                onClick = { onPresentationCommand("END") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x18FFFFFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("End Presentation (Esc)", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}
