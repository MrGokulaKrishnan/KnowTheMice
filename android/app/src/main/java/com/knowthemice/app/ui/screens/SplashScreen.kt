package com.knowthemice.app.ui.screens

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.knowthemice.app.R
import com.knowthemice.app.ui.theme.BgAmoled
import kotlinx.coroutines.delay

/**
 * Premium startup boot animation for Know The Mice.
 * Designed for 60-120 FPS fluid performance on AMOLED black.
 *
 * Sequence:
 * 1. AMOLED Black Background
 * 2. Know The Mice Logo appears (subtle fade + scale)
 * 3. KM / Mouse control energy pulse (subtle flame orange glow)
 * 4. Logo settles
 * 5. Smooth cross-fade to Home screen
 *
 * Respects system Reduced Motion settings for accessibility.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current

    // Check system animation / reduced motion preference
    val isReducedMotion = remember {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (_: Exception) {
            false
        }
    }

    var animationStarted by remember { mutableStateOf(false) }

    // Reduced motion fast fade
    val reducedAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "reduced_alpha"
    )

    // Full motion animatable properties
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.88f) }
    val glowAlpha = remember { Animatable(0f) }
    val glowScale = remember { Animatable(0.7f) }
    val containerAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        animationStarted = true

        if (isReducedMotion) {
            // Shortened transition: Black -> Logo -> Home
            delay(400)
            containerAlpha.animateTo(0f, tween(180))
            onSplashFinished()
        } else {
            // Step 1: Subtle Fade + Scale In (0 - 350ms)
            // Uses fast Apple-grade cubic-bezier for snappy response
            val easeOutQuint = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
            logoAlpha.animateTo(1f, tween(320, easing = easeOutQuint))
            logoScale.animateTo(1f, tween(360, easing = easeOutQuint))

            // Step 2: KM Energy Glow Pulse (300ms - 750ms)
            glowAlpha.animateTo(0.6f, tween(260, easing = FastOutSlowInEasing))
            glowScale.animateTo(1.15f, tween(320, easing = FastOutSlowInEasing))

            // Step 3: Logo Settles (750ms - 950ms)
            glowAlpha.animateTo(0.15f, tween(220, easing = LinearOutSlowInEasing))
            glowScale.animateTo(1.0f, tween(220, easing = LinearOutSlowInEasing))
            delay(80)

            // Step 4: Smooth transition to Home screen (950ms - 1100ms)
            containerAlpha.animateTo(0f, tween(200, easing = FastOutLinearInEasing))
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgAmoled)
            .alpha(containerAlpha.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    // Tap to skip immediately
                    onSplashFinished()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isReducedMotion) {
            // Reduced motion presentation: static logo, clean fast fade
            Image(
                painter = painterResource(id = R.drawable.logo_master),
                contentDescription = "Know The Mice",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(reducedAlpha),
                contentScale = ContentScale.Fit
            )
        } else {
            // Full GPU-accelerated graphicsLayer presentation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                // Subtle radial flame orange energy glow behind logo
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(glowScale.value)
                        .alpha(glowAlpha.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x90FF5E00),
                                    Color(0x40FF8A00),
                                    Color(0x00000000)
                                )
                            )
                        )
                )

                // Master Know The Mice Logo (Original aspect ratio, zero distortion)
                Image(
                    painter = painterResource(id = R.drawable.logo_master),
                    contentDescription = "Know The Mice",
                    modifier = Modifier
                        .size(210.dp)
                        .graphicsLayer {
                            alpha = logoAlpha.value
                            scaleX = logoScale.value
                            scaleY = logoScale.value
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
