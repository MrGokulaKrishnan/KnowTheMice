#!/usr/bin/env python3
"""
Know The Mice — Master Brand Asset Pipeline
Generates all canonical cross-platform branding assets from shared/logo.jpg.
"""

import os
import io
import math
from PIL import Image, ImageDraw

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
SOURCE_IMG = os.path.join(ROOT, "shared", "logo.jpg")

# Output directory structures
BRANDING_DIR = os.path.join(ROOT, "branding")
BRANDING_MASTER = os.path.join(BRANDING_DIR, "master")
BRANDING_WEB = os.path.join(BRANDING_DIR, "web")
BRANDING_ANDROID = os.path.join(BRANDING_DIR, "android")
BRANDING_WINDOWS = os.path.join(BRANDING_DIR, "windows")
BRANDING_LINUX = os.path.join(BRANDING_DIR, "linux")

WEBSITE_PUBLIC = os.path.join(ROOT, "website", "public")
ANDROID_RES = os.path.join(ROOT, "android", "app", "src", "main", "res")
WINDOWS_HOST = os.path.join(ROOT, "windows", "KnowTheMice.Host")
WINDOWS_SETUP = os.path.join(ROOT, "windows", "KnowTheMice.Setup")


def ensure_dirs():
    dirs = [
        BRANDING_MASTER, BRANDING_WEB, BRANDING_ANDROID, BRANDING_WINDOWS, BRANDING_LINUX,
        WEBSITE_PUBLIC,
        os.path.join(ANDROID_RES, "drawable"),
        os.path.join(ANDROID_RES, "drawable-mdpi"),
        os.path.join(ANDROID_RES, "drawable-hdpi"),
        os.path.join(ANDROID_RES, "drawable-xhdpi"),
        os.path.join(ANDROID_RES, "drawable-xxhdpi"),
        os.path.join(ANDROID_RES, "drawable-xxxhdpi"),
        os.path.join(ANDROID_RES, "mipmap-mdpi"),
        os.path.join(ANDROID_RES, "mipmap-hdpi"),
        os.path.join(ANDROID_RES, "mipmap-xhdpi"),
        os.path.join(ANDROID_RES, "mipmap-xxhdpi"),
        os.path.join(ANDROID_RES, "mipmap-xxxhdpi"),
        WINDOWS_HOST, WINDOWS_SETUP
    ]
    for d in dirs:
        os.makedirs(d, exist_ok=True)


def extract_transparent(source_img: Image.Image) -> Image.Image:
    """Extracts transparent PNG with clean anti-aliased alpha from black background."""
    img = source_img.convert("RGBA")
    pix = img.load()
    w, h = img.size
    trans = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    tpix = trans.load()

    for y in range(h):
        for x in range(w):
            r, g, b, a = pix[x, y]
            lum = max(r, g, b)
            if lum <= 12:
                tpix[x, y] = (0, 0, 0, 0)
            elif lum < 45:
                alpha = int(((lum - 12) / 33.0) * 255)
                tpix[x, y] = (r, g, b, alpha)
            else:
                tpix[x, y] = (r, g, b, 255)
    return trans


def crop_artwork(trans_img: Image.Image, y_start=0, y_end=1024):
    """Crops non-empty artwork bbox."""
    w, h = trans_img.size
    pix = trans_img.load()
    min_x, min_y, max_x, max_y = w, h, 0, 0
    found = False

    for y in range(max(0, y_start), min(h, y_end)):
        for x in range(w):
            _, _, _, a = pix[x, y]
            if a > 20:
                found = True
                if x < min_x: min_x = x
                if x > max_x: max_x = x
                if y < min_y: min_y = y
                if y > max_y: max_y = y

    if not found:
        return trans_img
    return trans_img.crop((min_x, min_y, max_x + 1, max_y + 1))


def place_in_canvas(cropped_img: Image.Image, canvas_size: int, scale_factor: float, bg_color=None):
    """Places cropped image centered in a square canvas scaled to safe-zone."""
    cw, ch = cropped_img.size
    max_dim = max(cw, ch)
    target_dim = int(canvas_size * scale_factor)

    scale = target_dim / max_dim
    new_w = max(1, int(cw * scale))
    new_h = max(1, int(ch * scale))

    resized = cropped_img.resize((new_w, new_h), Image.Resampling.LANCZOS)

    if bg_color is None:
        canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    else:
        canvas = Image.new("RGBA", (canvas_size, canvas_size), bg_color)

    offset_x = (canvas_size - new_w) // 2
    offset_y = (canvas_size - new_h) // 2
    canvas.paste(resized, (offset_x, offset_y), resized)
    return canvas


def make_round_icon(square_img: Image.Image) -> Image.Image:
    """Creates a circular masked icon for Android roundIcon."""
    size = square_img.size
    mask = Image.new("L", size, 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size[0], size[1]), fill=255)

    out = Image.new("RGBA", size, (0, 0, 0, 0))
    out.paste(square_img, (0, 0))
    out.putalpha(mask)
    return out


def make_rounded_icon(square_img: Image.Image, radius_pct: float = 0.15) -> Image.Image:
    """Creates a 15% rounded corner container icon for Android launcher icon."""
    size = square_img.size
    radius = int(min(size) * radius_pct)
    mask = Image.new("L", size, 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([0, 0, size[0], size[1]], radius=radius, fill=255)

    out = Image.new("RGBA", size, (0, 0, 0, 0))
    out.paste(square_img, (0, 0))
    out.putalpha(mask)
    return out


def create_tray_badge(trans_symbol: Image.Image, size: int = 256) -> Image.Image:
    """Creates an AMOLED black squircle badge (15% corner radius, no border) for system tray matching default logo."""
    badge = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    radius = int(size * 0.15)
    
    # 15% rounded black container with zero border
    mask = Image.new("L", (size, size), 0)
    draw_mask = ImageDraw.Draw(mask)
    draw_mask.rounded_rectangle([0, 0, size, size], radius=radius, fill=255)
    
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 255))
    badge.paste(bg, (0, 0), mask)

    pad = int(size * 0.12)
    inner_w = size - 2 * pad
    inner_h = size - 2 * pad
    sym_w, sym_h = trans_symbol.size
    scale = min(inner_w / sym_w, inner_h / sym_h)
    new_w = max(1, int(sym_w * scale))
    new_h = max(1, int(sym_h * scale))
    resized_sym = trans_symbol.resize((new_w, new_h), Image.Resampling.LANCZOS)

    pos_x = (size - new_w) // 2
    pos_y = (size - new_h) // 2
    badge.paste(resized_sym, (pos_x, pos_y), resized_sym)
    return badge


def make_ico_from_master(master_img: Image.Image, output_path: str, sizes: list):
    """Saves multiple RGBA resolutions into Windows .ICO format using PIL's sizes parameter."""
    master_rgba = master_img.convert("RGBA")
    size_tuples = [(s, s) for s in sizes]
    master_rgba.save(output_path, format="ICO", sizes=size_tuples)


def make_svg_wrapper(png_image: Image.Image, output_path: str):
    """Creates clean master SVG wrapping the lossless master artwork."""
    import base64
    buf = io.BytesIO()
    png_image.save(buf, format="PNG")
    b64 = base64.b64encode(buf.getvalue()).decode("ascii")
    w, h = png_image.size
    svg_content = f"""<svg width="{w}" height="{h}" viewBox="0 0 {w} {h}" fill="none" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect width="{w}" height="{h}" fill="#000000"/>
  <image width="{w}" height="{h}" xlink:href="data:image/png;base64,{b64}"/>
</svg>
"""
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(svg_content)


def make_svg_symbol_wrapper(png_image: Image.Image, output_path: str):
    """Creates clean SVG wrapper for the symbol."""
    import base64
    buf = io.BytesIO()
    png_image.save(buf, format="PNG")
    b64 = base64.b64encode(buf.getvalue()).decode("ascii")
    w, h = png_image.size
    svg_content = f"""<svg width="{w}" height="{h}" viewBox="0 0 {w} {h}" fill="none" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <image width="{w}" height="{h}" xlink:href="data:image/png;base64,{b64}"/>
</svg>
"""
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(svg_content)


def main():
    print(f"Loading authoritative master from {SOURCE_IMG}...")
    ensure_dirs()
    src = Image.open(SOURCE_IMG).convert("RGBA")

    # 1. Generate transparent master
    trans_master = extract_transparent(src)

    # 2. Extract components
    crop_full = crop_artwork(trans_master, y_start=0, y_end=850)
    crop_symbol = crop_artwork(trans_master, y_start=0, y_end=665)

    # 3. Canonical Master Assets
    master_full_black = place_in_canvas(crop_full, 1024, 0.86, bg_color=(0, 0, 0, 255))
    master_full_trans = place_in_canvas(crop_full, 1024, 0.86, bg_color=None)
    master_symbol_black = place_in_canvas(crop_symbol, 1024, 0.86, bg_color=(0, 0, 0, 255))
    master_symbol_trans = place_in_canvas(crop_symbol, 1024, 0.86, bg_color=None)

    master_full_black.save(os.path.join(BRANDING_MASTER, "know-the-mice-master.png"))
    master_full_trans.save(os.path.join(BRANDING_MASTER, "know-the-mice-transparent.png"))
    master_symbol_black.save(os.path.join(BRANDING_MASTER, "know-the-mice-symbol.png"))
    master_symbol_trans.save(os.path.join(BRANDING_MASTER, "know-the-mice-symbol-transparent.png"))

    make_svg_wrapper(master_full_black, os.path.join(BRANDING_MASTER, "know-the-mice-master.svg"))
    make_svg_symbol_wrapper(master_symbol_trans, os.path.join(BRANDING_MASTER, "know-the-mice-symbol.svg"))

    # Also save to shared/
    master_full_black.save(os.path.join(ROOT, "shared", "logo.png"))
    make_svg_wrapper(master_full_black, os.path.join(ROOT, "shared", "logo.svg"))
    make_svg_symbol_wrapper(master_symbol_trans, os.path.join(ROOT, "shared", "logo_icon.svg"))

    # 4. Web Assets
    # Full logo (square 1024x1024 on black)
    master_full_black.save(os.path.join(WEBSITE_PUBLIC, "logo.png"))
    master_symbol_trans.save(os.path.join(WEBSITE_PUBLIC, "logo_monogram.png"))

    # Favicon SVG (symbol on transparent)
    make_svg_symbol_wrapper(master_symbol_trans, os.path.join(WEBSITE_PUBLIC, "favicon.svg"))
    make_svg_symbol_wrapper(master_symbol_trans, os.path.join(BRANDING_WEB, "favicon.svg"))

    # Favicon PNGs
    fav_16 = place_in_canvas(crop_symbol, 16, 0.95, bg_color=None)
    fav_32 = place_in_canvas(crop_symbol, 32, 0.95, bg_color=None)
    fav_48 = place_in_canvas(crop_symbol, 48, 0.95, bg_color=None)
    fav_64 = place_in_canvas(crop_symbol, 64, 0.95, bg_color=None)

    fav_16.save(os.path.join(WEBSITE_PUBLIC, "favicon-16x16.png"))
    fav_32.save(os.path.join(WEBSITE_PUBLIC, "favicon-32x32.png"))
    fav_48.save(os.path.join(WEBSITE_PUBLIC, "favicon-48x48.png"))

    fav_16.save(os.path.join(BRANDING_WEB, "favicon-16x16.png"))
    fav_32.save(os.path.join(BRANDING_WEB, "favicon-32x32.png"))
    fav_48.save(os.path.join(BRANDING_WEB, "favicon-48x48.png"))

    # Favicon ICO (64, 48, 32, 24, 16)
    fav_master = place_in_canvas(crop_symbol, 256, 0.95, bg_color=None)
    make_ico_from_master(fav_master, os.path.join(WEBSITE_PUBLIC, "favicon.ico"), [64, 48, 32, 24, 16])
    make_ico_from_master(fav_master, os.path.join(BRANDING_WEB, "favicon.ico"), [64, 48, 32, 24, 16])

    # Apple Touch Icon (180x180, pure black background, logo centered)
    apple_icon = place_in_canvas(crop_full, 180, 0.80, bg_color=(0, 0, 0, 255))
    apple_icon.save(os.path.join(WEBSITE_PUBLIC, "apple-touch-icon.png"))
    apple_icon.save(os.path.join(BRANDING_WEB, "apple-touch-icon.png"))

    # PWA Icons (192, 512, on black background)
    pwa_192 = place_in_canvas(crop_full, 192, 0.80, bg_color=(0, 0, 0, 255))
    pwa_512 = place_in_canvas(crop_full, 512, 0.80, bg_color=(0, 0, 0, 255))
    pwa_192.save(os.path.join(WEBSITE_PUBLIC, "icon-192.png"))
    pwa_512.save(os.path.join(WEBSITE_PUBLIC, "icon-512.png"))
    pwa_192.save(os.path.join(BRANDING_WEB, "icon-192.png"))
    pwa_512.save(os.path.join(BRANDING_WEB, "icon-512.png"))

    # OpenGraph / Twitter Image (1200x630, pure black background)
    og_img = Image.new("RGBA", (1200, 630), (0, 0, 0, 255))
    logo_480 = place_in_canvas(crop_full, 480, 0.95, bg_color=None)
    og_img.paste(logo_480, ((1200 - 480) // 2, (630 - 480) // 2), logo_480)
    og_img.save(os.path.join(WEBSITE_PUBLIC, "og-image.png"))
    og_img.save(os.path.join(BRANDING_WEB, "og-image.png"))

    # 5. Android Adaptive Icon & Mipmaps
    # CRITICAL: Android Adaptive Icon Canvas is 108dp x 108dp.
    # Safe zone is a circle with diameter 66dp (61.1% of canvas).
    # Foreground MUST be centered inside this 66% circle with transparent padding!
    # Scale factor: 0.60 guarantees 100% visibility on all circular/squircle masks.
    android_fg_scales = {
        "mdpi": 108,
        "hdpi": 162,
        "xhdpi": 216,
        "xxhdpi": 324,
        "xxxhdpi": 432
    }
    for density, size in android_fg_scales.items():
        fg_asset = place_in_canvas(crop_full, size, 0.60, bg_color=None)
        fg_path = os.path.join(ANDROID_RES, f"drawable-{density}", "ic_launcher_foreground_asset.png")
        fg_asset.save(fg_path)

    # Base drawable foreground
    fg_base = place_in_canvas(crop_full, 432, 0.60, bg_color=None)
    fg_base.save(os.path.join(ANDROID_RES, "drawable", "ic_launcher_foreground_asset.png"))
    fg_base.save(os.path.join(BRANDING_ANDROID, "ic_launcher_foreground.png"))

    # In-app drawables
    master_symbol_trans.save(os.path.join(ANDROID_RES, "drawable", "logo_monogram.png"))
    master_full_black.save(os.path.join(ANDROID_RES, "drawable", "logo_master.png"))

    # Legacy Mipmaps (Android: exact artwork as uploaded in media, no artificial corner cutouts)
    mipmap_sizes = {
        "mdpi": 48,
        "hdpi": 72,
        "xhdpi": 96,
        "xxhdpi": 144,
        "xxxhdpi": 192
    }
    for density, size in mipmap_sizes.items():
        legacy_square = place_in_canvas(crop_full, size, 0.86, bg_color=(0, 0, 0, 255))
        legacy_round = make_round_icon(legacy_square)

        legacy_square.save(os.path.join(ANDROID_RES, f"mipmap-{density}", "ic_launcher.png"))
        legacy_round.save(os.path.join(ANDROID_RES, f"mipmap-{density}", "ic_launcher_round.png"))

    # 512x512 Master APK icon (SAME AS UPLOADED IN MEDIA)
    apk_512 = place_in_canvas(crop_full, 512, 0.86, bg_color=(0, 0, 0, 255))
    apk_512.save(os.path.join(BRANDING_ANDROID, "ic_launcher.png"))
    apk_512.save(os.path.join(ROOT, "shared", "apk_icon.png"))

    # 6. Windows Assets (WITH 15% RADIUS IN CORNER ONLY IN WINDOWS - AMOLED black, NO border)
    win_sizes = [256, 128, 64, 48, 40, 32, 24, 20, 16]
    win_master_15 = make_rounded_icon(master_full_black, 0.15)
    make_ico_from_master(win_master_15, os.path.join(WINDOWS_HOST, "icon.ico"), win_sizes)
    make_ico_from_master(win_master_15, os.path.join(WINDOWS_SETUP, "icon.ico"), win_sizes)
    make_ico_from_master(win_master_15, os.path.join(BRANDING_WINDOWS, "app.ico"), win_sizes)
    make_ico_from_master(win_master_15, os.path.join(BRANDING_WINDOWS, "installer.ico"), win_sizes)

    # Windows Tray Icon: AMOLED black squircle badge (15% corner radius, NO border)
    # Contains all Windows taskbar DPI resolutions: 16, 20, 24, 32, 48, 64
    tray_badge_master = create_tray_badge(master_symbol_trans, 256)
    tray_badge_master.save(os.path.join(BRANDING_WINDOWS, "tray_badge.png"))
    tray_sizes = [16, 20, 24, 32, 48, 64]
    make_ico_from_master(tray_badge_master, os.path.join(WINDOWS_HOST, "tray.ico"), tray_sizes)
    make_ico_from_master(tray_badge_master, os.path.join(BRANDING_WINDOWS, "tray.ico"), tray_sizes)

    # Windows Host & Setup window logos (15% corner radius, NO border)
    win_master_15.save(os.path.join(WINDOWS_HOST, "logo.png"))
    win_master_15.save(os.path.join(WINDOWS_SETUP, "logo.png"))

    # 7. Linux Assets
    make_svg_wrapper(master_full_black, os.path.join(BRANDING_LINUX, "knowthemice.svg"))
    linux_sizes = [16, 24, 32, 48, 64, 128, 256, 512]
    for s in linux_sizes:
        icon_linux = place_in_canvas(crop_full, s, 0.86, bg_color=(0, 0, 0, 255))
        icon_linux.save(os.path.join(BRANDING_LINUX, f"knowthemice-{s}.png"))
    master_full_black.save(os.path.join(BRANDING_LINUX, "knowthemice.png"))

    desktop_entry = """[Desktop Entry]
Name=Know The Mice
Comment=Seamless Android to PC Remote Control
Exec=knowthemice
Icon=knowthemice
Terminal=false
Type=Application
Categories=Utility;RemoteAccess;
Keywords=Remote;Mouse;Keyboard;Trackpad;
"""
    with open(os.path.join(BRANDING_LINUX, "knowthemice.desktop"), "w", encoding="utf-8") as f:
        f.write(desktop_entry)

    print("Brand asset pipeline successfully generated all cross-platform assets!")


if __name__ == "__main__":
    main()
