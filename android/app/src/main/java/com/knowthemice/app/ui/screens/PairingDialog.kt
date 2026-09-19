package com.knowthemice.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.knowthemice.app.ui.components.GlassSurface
import com.knowthemice.app.ui.components.GradientButton
import com.knowthemice.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PairingDialog(
    hostName: String,
    onSubmitPin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableStateOf(60) }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (remainingSeconds == 0) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ENTER 6-DIGIT PIN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A pairing code has appeared on $hostName. Enter it below to securely pair your phone.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    singleLine = true,
                    placeholder = { Text("000000", color = TextMuted, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandHighlight
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandHighlight,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Expires in ${remainingSeconds}s",
                    color = BrandOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitPin(pin) },
                enabled = pin.length == 6,
                colors = ButtonDefaults.buttonColors(containerColor = BrandHighlight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("VERIFY", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = BgSurface,
        shape = RoundedCornerShape(20.dp)
    )
}
