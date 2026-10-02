package com.knowthemice.app.ui.screens

import android.graphics.PathMeasure
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.knowthemice.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Visual System & Design Tokens
private val AmoledBlack = Color(0xFF000000)
private val BrandPrimary = Color(0xFFFF5A00)
private val BrandLight = Color(0xFFFF8A00)
private val BrandPaleGlow = Color(0xFFFFB066)

private val EaseOutCubicCurve = CubicBezierEasing(0.33f, 1.0f, 0.68f, 1.0f)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Accessibility & Reduced Motion Requirement
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

    // Reduced motion fast fade
    val reducedAlpha = remember { Animatable(0f) }

    // Phase 1 & 4-5: Ambient Glow Behind Center
    val ambientGlowAlpha = remember { Animatable(0f) }
    val ambientGlowScale = remember { Animatable(1f) }

    // Phase 2: Central Energy Ignition Point
    val ignitionScale = remember { Animatable(0.2f) }
    val ignitionAlpha = remember { Animatable(0f) }

    // Phase 3: Energy Shockwave Ripple
    val rippleRadiusDp = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    // Phase 4 & 5: Logo Container & Artwork Reveal
    val containerAlpha = remember { Animatable(0f) }
    val containerScale = remember { Animatable(0.92f) }

    // Phase 6: Perimeter Laser Border Draw
    val laserProgress = remember { Animatable(0f) }
    val persistentBorderAlpha = remember { Animatable(0f) }

    // Phase 7 & 8: Orbiting High-Energy Comet Particle & Stabilization
    val cometProgress = remember { Animatable(0f) }
    val cometAlpha = remember { Animatable(0f) }

    // Phase 9: Diagonal Glossy Light Sweep Sheen
    val sheenProgress = remember { Animatable(-0.5f) }

    // Phase 10: Seamless Application Reveal Exit
    val presentationScale = remember { Animatable(1.0f) }
    val presentationAlpha = remember { Animatable(1.0f) }

    LaunchedEffect(Unit) {
        if (isReducedMotion) {
            // Bypass multi-phase timeline: fade in logo over 250ms, wait 200ms, navigate immediately to Home
            reducedAlpha.animateTo(1f, tween(durationMillis = 250, easing = LinearEasing))
            delay(200)
            onSplashFinished()
            return@LaunchedEffect
        }

        // Orchestrated 10-Phase Coroutine Timeline (~1640ms)

        // PHASE 1 — PURE BLACK INITIALIZATION & AMBIENT GLOW (0ms - 400ms)
        launch {
            ambientGlowAlpha.animateTo(0.45f, tween(durationMillis = 400, easing = EaseOutCubicCurve))
        }

        // PHASE 2 — CENTRAL ENERGY IGNITION POINT (60ms - 320ms)
        launch {
            delay(60)
            launch {
                ignitionScale.animateTo(1.6f, tween(durationMillis = 300, easing = FastOutSlowInEasing))
            }
            launch {
                ignitionAlpha.animateTo(1.0f, tween(durationMillis = 160, easing = LinearEasing))
                delay(80)
                ignitionAlpha.animateTo(0f, tween(durationMillis = 140, easing = LinearEasing))
            }
        }

        // PHASE 3 — ENERGY SHOCKWAVE RIPPLE (180ms - 540ms)
        launch {
            delay(180)
            launch {
                rippleRadiusDp.animateTo(150f, tween(durationMillis = 380, easing = EaseOutCubicCurve))
            }
            launch {
                rippleAlpha.animateTo(0.85f, tween(durationMillis = 120, easing = LinearEasing))
                rippleAlpha.animateTo(0f, tween(durationMillis = 260, easing = EaseOutCubicCurve))
            }
        }

        // PHASE 4 & 5 — LOGO CONTAINER & ARTWORK REVEAL (240ms - 640ms)
        launch {
            delay(240)
            launch {
                containerAlpha.animateTo(1.0f, tween(durationMillis = 320, easing = FastOutSlowInEasing))
            }
            launch {
                containerScale.animateTo(1.0f, tween(durationMillis = 360, easing = FastOutSlowInEasing))
            }
            launch {
                // Glow pulses: alpha expands to 0.75, settles to 0.32
                ambientGlowAlpha.animateTo(0.75f, tween(durationMillis = 180, easing = FastOutSlowInEasing))
                ambientGlowAlpha.animateTo(0.32f, tween(durationMillis = 220, easing = FastOutSlowInEasing))
            }
            launch {
                // Radius expands to 1.35x, settles to 1.10x
                ambientGlowScale.animateTo(1.35f, tween(durationMillis = 180, easing = FastOutSlowInEasing))
                ambientGlowScale.animateTo(1.10f, tween(durationMillis = 220, easing = FastOutSlowInEasing))
            }
        }

        // PHASE 6 — PERIMETER LASER BORDER DRAW (400ms - 880ms)
        launch {
            delay(400)
            laserProgress.animateTo(1f, tween(durationMillis = 460, easing = FastOutSlowInEasing))
            persistentBorderAlpha.snapTo(0.85f)
        }

        // PHASE 7 & 8 — ORBITING HIGH-ENERGY COMET PARTICLE (680ms - 1160ms) & STABILIZATION (1160ms - 1420ms)
        launch {
            delay(680)
            cometAlpha.snapTo(1f)
            cometProgress.animateTo(1f, tween(durationMillis = 480, easing = FastOutSlowInEasing))
            // Phase 8: Particle dissolves smoothly, logo and perimeter border remain stable
            cometAlpha.animateTo(0f, tween(durationMillis = 120, easing = FastOutSlowInEasing))
        }

        // PHASE 9 — DIAGONAL GLOSSY LIGHT SWEEP SHEEN (940ms - 1320ms)
        launch {
            delay(940)
            sheenProgress.animateTo(1.5f, tween(durationMillis = 360, easing = FastOutSlowInEasing))
        }

        // PHASE 10 — SEAMLESS APPLICATION REVEAL EXIT (1420ms - 1640ms)
        launch {
            delay(1420)
            launch {
                presentationScale.animateTo(0.96f, tween(durationMillis = 220, easing = FastOutSlowInEasing))
            }
            launch {
                presentationAlpha.animateTo(0f, tween(durationMillis = 220, easing = FastOutSlowInEasing))
            }
        }

        // Complete sequence at 1640ms
        delay(1640)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
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
            // Accessible reduced motion presentation
            Box(
                modifier = Modifier
                    .size(176.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(AmoledBlack)
                    .padding(14.dp)
                    .alpha(reducedAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_master),
                    contentDescription = "Know The Mice",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            // Full GPU-accelerated cinematic presentation container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(presentationAlpha.value)
                    .scale(presentationScale.value),
                contentAlignment = Alignment.Center
            ) {
                // PHASE 1 & 4-5: Radial Ambient Glow Behind Center
                if (ambientGlowAlpha.value > 0f) {
                    val glowSize = 280.dp * ambientGlowScale.value
                    Box(
                        modifier = Modifier
                            .size(glowSize)
                            .alpha(ambientGlowAlpha.value)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        BrandPrimary.copy(alpha = 0.65f),
                                        BrandLight.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // PHASE 2: Central Energy Ignition Point
                if (ignitionAlpha.value > 0f) {
                    val ignitionSize = 80.dp * ignitionScale.value
                    Box(
                        modifier = Modifier
                            .size(ignitionSize)
                            .alpha(ignitionAlpha.value)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        BrandLight,
                                        BrandPrimary,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // PHASE 3: Energy Shockwave Ripple Wave
                if (rippleAlpha.value > 0f && rippleRadiusDp.value > 0f) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.size(300.dp)
                    ) {
                        val strokePx = with(density) { 2.5.dp.toPx() }
                        val radiusPx = with(density) { rippleRadiusDp.value.dp.toPx() }
                        drawCircle(
                            color = BrandLight.copy(alpha = rippleAlpha.value),
                            radius = radiusPx,
                            center = center,
                            style = Stroke(width = strokePx)
                        )
                    }
                }

                // PHASE 4-9: Logo Container (176dp x 176dp, 26dp Corner Radius = 15%)
                Box(
                    modifier = Modifier
                        .size(176.dp)
                        .scale(containerScale.value)
                        .alpha(containerAlpha.value),
                    contentAlignment = Alignment.Center
                ) {
                    // Container Surface (#000000 with 26dp rounded corners)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(26.dp))
                            .background(AmoledBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        // Logo Artwork: Scaled inside container with 14dp padding, ContentScale.Fit
                        Image(
                            painter = painterResource(id = R.drawable.logo_master),
                            contentDescription = "Know The Mice",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            contentScale = ContentScale.Fit
                        )

                        // PHASE 9: Diagonal Glossy Light Sweep Sheen (-0.5f to 1.5f progress)
                        if (sheenProgress.value in -0.5f..1.5f) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val sweepOffset = (w + h) * sheenProgress.value
                                val bandWidth = w * 0.55f

                                val startX = sweepOffset - bandWidth
                                val startY = sweepOffset - bandWidth
                                val endX = sweepOffset + bandWidth
                                val endY = sweepOffset + bandWidth

                                drawRect(
                                    brush = Brush.linearGradient(
                                        colorStops = arrayOf(
                                            0.0f to Color.Transparent,
                                            0.35f to Color.White.copy(alpha = 0.13f),
                                            0.50f to BrandPaleGlow.copy(alpha = 0.33f),
                                            0.65f to Color.White.copy(alpha = 0.13f),
                                            1.0f to Color.Transparent
                                        ),
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY)
                                    )
                                )
                            }
                        }
                    }

                    // PHASE 6, 7 & 8: Perimeter Laser Border Draw & Orbiting Comet Particle
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth18Px = with(density) { 1.8.dp.toPx() }
                        val cornerRadiusPx = with(density) { 26.dp.toPx() }
                        val halfStroke = strokeWidth18Px / 2f

                        // 15% Rounded Rectangle Perimeter Path
                        val composePath = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = halfStroke,
                                    top = halfStroke,
                                    right = size.width - halfStroke,
                                    bottom = size.height - halfStroke,
                                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                                )
                            )
                        }

                        val androidPath = composePath.asAndroidPath()
                        val pathMeasure = PathMeasure(androidPath, true)
                        val totalLength = pathMeasure.length

                        // PHASE 6: Progressive Laser Border Draw (0% -> 100%)
                        if (laserProgress.value > 0f && laserProgress.value < 1f) {
                            val laserAndroidPath = android.graphics.Path()
                            pathMeasure.getSegment(0f, totalLength * laserProgress.value, laserAndroidPath, true)
                            drawPath(
                                path = laserAndroidPath.asComposePath(),
                                brush = Brush.linearGradient(
                                    listOf(BrandPrimary, BrandLight, BrandPrimary)
                                ),
                                style = Stroke(width = strokeWidth18Px, cap = StrokeCap.Round)
                            )
                        }

                        // Persistent Subtle Accent Border (1.5dp, alpha: 0.85) once 100% reached
                        if (persistentBorderAlpha.value > 0f) {
                            val persistentStrokePx = with(density) { 1.5.dp.toPx() }
                            drawPath(
                                path = composePath,
                                brush = Brush.linearGradient(
                                    listOf(
                                        BrandPrimary.copy(alpha = 0.85f * persistentBorderAlpha.value),
                                        BrandLight.copy(alpha = 0.85f * persistentBorderAlpha.value),
                                        BrandPrimary.copy(alpha = 0.85f * persistentBorderAlpha.value)
                                    )
                                ),
                                style = Stroke(width = persistentStrokePx)
                            )
                        }

                        // PHASE 7: Orbiting High-Energy Comet Particle (680ms - 1160ms)
                        if (cometAlpha.value > 0f && totalLength > 0f) {
                            val pos = FloatArray(2)
                            val tan = FloatArray(2)
                            val headDist = (totalLength * (cometProgress.value % 1f)).coerceIn(0f, totalLength)
                            pathMeasure.getPosTan(headDist, pos, tan)
                            val headCenter = Offset(pos[0], pos[1])

                            // Trailing Arc: spanning 16% of perimeter length
                            val tailLength = 0.16f * totalLength
                            val tailStart = headDist - tailLength

                            if (tailStart >= 0f) {
                                val tailAndroidPath = android.graphics.Path()
                                pathMeasure.getSegment(tailStart, headDist, tailAndroidPath, true)
                                drawPath(
                                    path = tailAndroidPath.asComposePath(),
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            BrandPrimary.copy(alpha = 0.70f * cometAlpha.value),
                                            Color.White.copy(alpha = 0.95f * cometAlpha.value)
                                        ),
                                        start = Offset(pos[0] - tan[0] * tailLength, pos[1] - tan[1] * tailLength),
                                        end = headCenter
                                    ),
                                    style = Stroke(width = with(density) { 2.2.dp.toPx() }, cap = StrokeCap.Round)
                                )
                            } else {
                                val tailPart1 = android.graphics.Path()
                                pathMeasure.getSegment(totalLength + tailStart, totalLength, tailPart1, true)
                                val tailPart2 = android.graphics.Path()
                                pathMeasure.getSegment(0f, headDist, tailPart2, true)
                                drawPath(
                                    path = tailPart1.asComposePath(),
                                    color = BrandPrimary.copy(alpha = 0.45f * cometAlpha.value),
                                    style = Stroke(width = with(density) { 2.0.dp.toPx() }, cap = StrokeCap.Round)
                                )
                                drawPath(
                                    path = tailPart2.asComposePath(),
                                    color = Color.White.copy(alpha = 0.90f * cometAlpha.value),
                                    style = Stroke(width = with(density) { 2.2.dp.toPx() }, cap = StrokeCap.Round)
                                )
                            }

                            // Comet Head: Soft outer aura (14dp radius)
                            drawCircle(
                                color = BrandPrimary.copy(alpha = 0.25f * cometAlpha.value),
                                radius = with(density) { 14.dp.toPx() },
                                center = headCenter
                            )

                            // Halo (7dp radius)
                            drawCircle(
                                color = BrandLight.copy(alpha = 0.75f * cometAlpha.value),
                                radius = with(density) { 7.dp.toPx() },
                                center = headCenter
                            )

                            // Solid white core (3.5dp radius)
                            drawCircle(
                                color = Color.White.copy(alpha = cometAlpha.value),
                                radius = with(density) { 3.5.dp.toPx() },
                                center = headCenter
                            )
                        }
                    }
                }
            }
        }
    }
}
