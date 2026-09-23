package com.knowthemice.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BgAmoled = Color(0xFF000000)
val BgSurface = Color(0xFF0A0A0A)
val BgCard = Color(0xFF0D0D0D)

val BrandOrange = Color(0xFFFF5A00)
val BrandHighlight = Color(0xFFFF8A00)
val BrandDeep = Color(0xFFD93600)
val BrandAmber = Color(0xFFFFB347)

val GlassFill = Color(0x0AFFFFFF)
val GlassBorder = Color(0x47FFA550)
val GlassBorderHover = Color(0x80FFA550)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA1A1AA)
val TextMuted = Color(0xFF71717A)

val StatusSuccess = Color(0xFF22C55E)
val StatusWarning = Color(0xFFF59E0B)
val StatusError = Color(0xFFEF4444)

val BrandGradient = Brush.linearGradient(
    colors = listOf(BrandHighlight, BrandOrange, BrandDeep)
)

val PrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFF8A00), Color(0xFFFF5A00), Color(0xFFD93600))
)

val SecondaryGradient = Brush.linearGradient(
    colors = listOf(Color(0x29FF8A00), Color(0x0DD93600))
)

val AccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFA333), Color(0xFFFF5A00))
)

val TextGradient = PrimaryGradient

val BorderGradient = Brush.linearGradient(
    colors = listOf(Color(0x73FF8A00), Color(0x33FF5A00), Color(0x59D93600))
)

val SurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xB3141414), Color(0xE60A0A0A))
)

val HoverGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFA033), Color(0xFFFF6B1A), Color(0xFFE64000))
)

val ActiveGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE65000), Color(0xFFCC4500), Color(0xFFB32D00))
)

val DisabledGradient = Brush.linearGradient(
    colors = listOf(Color(0x14FFFFFF), Color(0x08FFFFFF))
)

val GlassPanelGradient = Brush.linearGradient(
    colors = listOf(Color(0x24FF8A00), Color(0x0AFFFFFF))
)
