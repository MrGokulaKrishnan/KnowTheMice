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

val GlassPanelGradient = Brush.linearGradient(
    colors = listOf(Color(0x24FF8A00), Color(0x0AFFFFFF))
)
