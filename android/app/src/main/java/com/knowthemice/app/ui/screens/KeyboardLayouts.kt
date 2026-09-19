package com.knowthemice.app.ui.screens

object WindowsVirtualKeys {
    const val VK_BACK = 0x08
    const val VK_TAB = 0x09
    const val VK_RETURN = 0x0D
    const val VK_SHIFT = 0x10
    const val VK_CONTROL = 0x11
    const val VK_MENU = 0x12 // ALT
    const val VK_CAPITAL = 0x14 // CAPS LOCK
    const val VK_ESCAPE = 0x1B
    const val VK_SPACE = 0x20
    const val VK_PRIOR = 0x21 // Page Up
    const val VK_NEXT = 0x22  // Page Down
    const val VK_END = 0x23
    const val VK_HOME = 0x24
    const val VK_LEFT = 0x25
    const val VK_UP = 0x26
    const val VK_RIGHT = 0x27
    const val VK_DOWN = 0x28
    const val VK_SNAPSHOT = 0x2C // Print Screen
    const val VK_INSERT = 0x2D
    const val VK_DELETE = 0x2E
    const val VK_LWIN = 0x5B

    val F_KEYS = (1..12).map { i -> "F$i" to (0x6F + i) }
}

data class KeyItem(
    val label: String,
    val subLabel: String = "",
    val vk: Int = 0,
    val char: String = "",
    val weight: Float = 1.0f,
    val isModifier: Boolean = false,
    val isAction: Boolean = false
)

object KeyboardLayouts {
    val ROW_FN = listOf(
        KeyItem("ESC", vk = WindowsVirtualKeys.VK_ESCAPE, weight = 1.1f, isAction = true),
        KeyItem("F1", vk = 0x70), KeyItem("F2", vk = 0x71), KeyItem("F3", vk = 0x72),
        KeyItem("F4", vk = 0x73), KeyItem("F5", vk = 0x74), KeyItem("F6", vk = 0x75),
        KeyItem("F7", vk = 0x76), KeyItem("F8", vk = 0x77), KeyItem("F9", vk = 0x78),
        KeyItem("F10", vk = 0x79), KeyItem("F11", vk = 0x7A), KeyItem("F12", vk = 0x7B)
    )

    val ROW_NUMBERS = listOf(
        KeyItem("`", subLabel = "~", vk = 0xC0),
        KeyItem("1", subLabel = "!", vk = 0x31),
        KeyItem("2", subLabel = "@", vk = 0x32),
        KeyItem("3", subLabel = "#", vk = 0x33),
        KeyItem("4", subLabel = "$", vk = 0x34),
        KeyItem("5", subLabel = "%", vk = 0x35),
        KeyItem("6", subLabel = "^", vk = 0x36),
        KeyItem("7", subLabel = "&", vk = 0x37),
        KeyItem("8", subLabel = "*", vk = 0x38),
        KeyItem("9", subLabel = "(", vk = 0x39),
        KeyItem("0", subLabel = ")", vk = 0x30),
        KeyItem("-", subLabel = "_", vk = 0xBD),
        KeyItem("=", subLabel = "+", vk = 0xBB),
        KeyItem("⌫", vk = WindowsVirtualKeys.VK_BACK, weight = 1.3f, isAction = true)
    )

    val ROW_QWERTY = listOf(
        KeyItem("TAB", vk = WindowsVirtualKeys.VK_TAB, weight = 1.3f, isAction = true),
        KeyItem("Q", vk = 0x51), KeyItem("W", vk = 0x57), KeyItem("E", vk = 0x45),
        KeyItem("R", vk = 0x52), KeyItem("T", vk = 0x54), KeyItem("Y", vk = 0x59),
        KeyItem("U", vk = 0x55), KeyItem("I", vk = 0x49), KeyItem("O", vk = 0x4F),
        KeyItem("P", vk = 0x50), KeyItem("[", subLabel = "{", vk = 0xDB),
        KeyItem("]", subLabel = "}", vk = 0xDD),
        KeyItem("\\", subLabel = "|", vk = 0xDC, weight = 1.1f)
    )

    val ROW_ASDF = listOf(
        KeyItem("CAPS", vk = WindowsVirtualKeys.VK_CAPITAL, weight = 1.4f, isModifier = true),
        KeyItem("A", vk = 0x41), KeyItem("S", vk = 0x53), KeyItem("D", vk = 0x44),
        KeyItem("F", vk = 0x46), KeyItem("G", vk = 0x47), KeyItem("H", vk = 0x48),
        KeyItem("J", vk = 0x4A), KeyItem("K", vk = 0x4B), KeyItem("L", vk = 0x4C),
        KeyItem(";", subLabel = ":", vk = 0xBA),
        KeyItem("'", subLabel = "\"", vk = 0xDE),
        KeyItem("ENTER", vk = WindowsVirtualKeys.VK_RETURN, weight = 1.6f, isAction = true)
    )

    val ROW_ZXCV = listOf(
        KeyItem("SHIFT", vk = WindowsVirtualKeys.VK_SHIFT, weight = 1.6f, isModifier = true),
        KeyItem("Z", vk = 0x5A), KeyItem("X", vk = 0x58), KeyItem("C", vk = 0x43),
        KeyItem("V", vk = 0x56), KeyItem("B", vk = 0x42), KeyItem("N", vk = 0x4E),
        KeyItem("M", vk = 0x4D), KeyItem(",", subLabel = "<", vk = 0xBC),
        KeyItem(".", subLabel = ">", vk = 0xBE),
        KeyItem("/", subLabel = "?", vk = 0xBF),
        KeyItem("DEL", vk = WindowsVirtualKeys.VK_DELETE, weight = 1.3f, isAction = true)
    )

    val ROW_BOTTOM = listOf(
        KeyItem("CTRL", vk = WindowsVirtualKeys.VK_CONTROL, weight = 1.2f, isModifier = true),
        KeyItem("WIN", vk = WindowsVirtualKeys.VK_LWIN, weight = 1.1f, isModifier = true),
        KeyItem("ALT", vk = WindowsVirtualKeys.VK_MENU, weight = 1.1f, isModifier = true),
        KeyItem("SPACE", vk = WindowsVirtualKeys.VK_SPACE, weight = 3.5f),
        KeyItem("ALT", vk = WindowsVirtualKeys.VK_MENU, weight = 1.1f, isModifier = true),
        KeyItem("◀", vk = WindowsVirtualKeys.VK_LEFT, weight = 1.0f, isAction = true),
        KeyItem("▲", vk = WindowsVirtualKeys.VK_UP, weight = 1.0f, isAction = true),
        KeyItem("▼", vk = WindowsVirtualKeys.VK_DOWN, weight = 1.0f, isAction = true),
        KeyItem("▶", vk = WindowsVirtualKeys.VK_RIGHT, weight = 1.0f, isAction = true)
    )
}
