package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.theme.*

@Composable
fun SettingsScreen(
    mouseSensitivity: Float,
    onSensitivityChange: (Float) -> Unit,
    naturalScrolling: Boolean,
    onNaturalScrollingChange: (Boolean) -> Unit,
    hapticFeedback: Boolean,
    onHapticFeedbackChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
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
            Text("SETTINGS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("MOUSE & TOUCHPAD", color = BrandHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Sensitivity: ${String.format("%.1f", mouseSensitivity)}x", color = Color.White, fontSize = 13.sp)
                        Slider(
                            value = mouseSensitivity,
                            onValueChange = onSensitivityChange,
                            valueRange = 0.2f..3.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandHighlight,
                                activeTrackColor = BrandOrange
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Natural Two-Finger Scrolling", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = naturalScrolling,
                                onCheckedChange = onNaturalScrollingChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = BrandHighlight, checkedTrackColor = BrandDeep)
                            )
                        }
                    }
                }
            }

            item {
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("HAPTICS & FEEDBACK", color = BrandHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Vibration on Click & Gestures", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = hapticFeedback,
                                onCheckedChange = onHapticFeedbackChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = BrandHighlight, checkedTrackColor = BrandDeep)
                            )
                        }
                    }
                }
            }

            item {
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("ABOUT KNOW THE MICE", color = BrandHighlight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Version 1.0.0 (Release GA)", color = Color.White, fontSize = 13.sp)
                        Text("Zero Cloud · Local-first encrypted communication", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}
