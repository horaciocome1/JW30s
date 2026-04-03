 #!/usr/bin/env python3
"""Generate JW30s app icon in all required sizes using Pillow."""

from PIL import Image, ImageDraw, ImageFont
import math
import os

def draw_rounded_rect(draw, xy, radius, fill):
    """Draw a rounded rectangle."""
    x0, y0, x1, y1 = xy
    # Four corners
    draw.pieslice([x0, y0, x0 + 2*radius, y0 + 2*radius], 180, 270, fill=fill)
    draw.pieslice([x1 - 2*radius, y0, x1, y0 + 2*radius], 270, 360, fill=fill)
    draw.pieslice([x0, y1 - 2*radius, x0 + 2*radius, y1], 90, 180, fill=fill)
    draw.pieslice([x1 - 2*radius, y1 - 2*radius, x1, y1], 0, 90, fill=fill)
    # Fill rectangles
    draw.rectangle([x0 + radius, y0, x1 - radius, y1], fill=fill)
    draw.rectangle([x0, y0 + radius, x1, y1 - radius], fill=fill)


def create_icon(size=1024):
    """Create the app icon at the given size."""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    s = size  # shorthand
    
    # --- Background: gradient green rounded square ---
    # Create gradient
    bg = Image.new('RGBA', (s, s), (0, 0, 0, 0))
    bg_draw = ImageDraw.Draw(bg)
    
    # Draw gradient line by line within rounded rect mask
    corner_r = int(s * 0.176)  # ~90/512
    
    # First fill the rounded rect with a base color, then overlay gradient
    for y in range(s):
        t = y / s
        # Gradient from #4CAF50 (top) to #2E7D32 (bottom)
        r = int(76 + (46 - 76) * t)
        g = int(175 + (125 - 175) * t)
        b = int(80 + (50 - 80) * t)
        bg_draw.line([(0, y), (s - 1, y)], fill=(r, g, b, 255))
    
    # Create rounded rect mask
    mask = Image.new('L', (s, s), 0)
    mask_draw = ImageDraw.Draw(mask)
    draw_rounded_rect(mask_draw, [0, 0, s, s], corner_r, 255)
    
    bg.putalpha(mask)
    img = Image.alpha_composite(img, bg)
    draw = ImageDraw.Draw(img)
    
    # --- Subtle radial shine overlay ---
    shine = Image.new('RGBA', (s, s), (0, 0, 0, 0))
    cx, cy = int(s * 0.4), int(s * 0.3)
    max_r = int(s * 0.7)
    for py in range(s):
        for px in range(s):
            dist = math.sqrt((px - cx)**2 + (py - cy)**2)
            if dist < max_r:
                alpha = int(25 * (1 - dist / max_r))
                shine.putpixel((px, py), (255, 255, 255, alpha))
    
    shine_masked = Image.new('RGBA', (s, s), (0, 0, 0, 0))
    shine_masked.paste(shine, mask=mask)
    img = Image.alpha_composite(img, shine_masked)
    draw = ImageDraw.Draw(img)
    
    # --- Hourglass ---
    cx, cy = s // 2, s // 2  # center
    
    white = (255, 255, 255, 255)
    white_glass = (255, 255, 255, 60)  # translucent glass
    white_sand = (255, 255, 255, 140)  # sand
    white_stream = (255, 255, 255, 150)
    
    # Scale factors based on size
    def sc(v):
        return int(v * s / 512)
    
    # Top plate
    plate_w = sc(210)
    plate_h = sc(12)
    plate_r = sc(4)
    draw_rounded_rect(draw, 
        [cx - plate_w//2, cy - sc(180), cx + plate_w//2, cy - sc(180) + plate_h],
        plate_r, white)
    
    # Bottom plate
    draw_rounded_rect(draw,
        [cx - plate_w//2, cy + sc(168), cx + plate_w//2, cy + sc(168) + plate_h],
        plate_r, white)
    
    # Decorative knobs on plates
    knob_r = sc(8)
    # Top knobs
    draw.ellipse([cx - plate_w//2 - knob_r + sc(6), cy - sc(180) - knob_r + sc(5),
                  cx - plate_w//2 + knob_r + sc(6), cy - sc(180) + knob_r + sc(5)], fill=white)
    draw.ellipse([cx + plate_w//2 - knob_r - sc(6), cy - sc(180) - knob_r + sc(5),
                  cx + plate_w//2 + knob_r - sc(6), cy - sc(180) + knob_r + sc(5)], fill=white)
    # Bottom knobs
    draw.ellipse([cx - plate_w//2 - knob_r + sc(6), cy + sc(168) - knob_r + sc(7),
                  cx - plate_w//2 + knob_r + sc(6), cy + sc(168) + knob_r + sc(7)], fill=white)
    draw.ellipse([cx + plate_w//2 - knob_r - sc(6), cy + sc(168) - knob_r + sc(7),
                  cx + plate_w//2 + knob_r - sc(6), cy + sc(168) + knob_r + sc(7)], fill=white)
    
    # Hourglass glass body - using polygons
    # Top half: wide at top, narrow at center
    top_wide = sc(82)
    neck = sc(14)
    top_y = cy - sc(166)
    mid_y = cy - sc(4)
    bot_y = cy + sc(4)
    bottom_y = cy + sc(166)
    
    # Top glass outline + fill
    top_glass = [
        (cx - top_wide, top_y),
        (cx + top_wide, top_y),
        (cx + neck, mid_y),
        (cx - neck, mid_y),
    ]
    draw.polygon(top_glass, fill=white_glass, outline=white, width=max(sc(5), 1))
    
    # Bottom glass outline + fill
    bot_glass = [
        (cx - neck, bot_y),
        (cx + neck, bot_y),
        (cx + top_wide, bottom_y),
        (cx - top_wide, bottom_y),
    ]
    draw.polygon(bot_glass, fill=white_glass, outline=white, width=max(sc(5), 1))
    
    # Neck connection (smooth the join)
    draw.ellipse([cx - neck - sc(2), cy - sc(12), cx + neck + sc(2), cy + sc(12)],
                 fill=white_glass, outline=white, width=max(sc(4), 1))
    
    # --- Sand in top chamber (partial - sand has been falling) ---
    sand_top_w = sc(42)
    sand_top_y = cy - sc(100)
    sand_neck = sc(10)
    sand_mid_y = cy - sc(30)
    top_sand = [
        (cx - sand_top_w, sand_top_y),
        (cx + sand_top_w, sand_top_y),
        (cx + sand_neck, sand_mid_y),
        (cx - sand_neck, sand_mid_y),
    ]
    draw.polygon(top_sand, fill=white_sand)
    
    # Flat top of sand
    draw.line([(cx - sand_top_w, sand_top_y), (cx + sand_top_w, sand_top_y)],
              fill=white_sand, width=max(sc(3), 1))
    
    # --- Sand stream through neck ---
    stream_w = sc(4)
    draw.rectangle([cx - stream_w//2, cy - sc(28), cx + stream_w//2, cy + sc(28)],
                   fill=white_stream)
    
    # --- Sand pile in bottom chamber ---
    sand_pile_w = sc(58)
    sand_pile_y = bottom_y
    sand_pile_neck = sc(12)
    sand_pile_mid = cy + sc(55)
    pile = [
        (cx - sand_pile_neck, sand_pile_mid),
        (cx + sand_pile_neck, sand_pile_mid),
        (cx + sand_pile_w, sand_pile_y),
        (cx - sand_pile_w, sand_pile_y),
    ]
    draw.polygon(pile, fill=white_sand)
    
    # --- "30" text ---
    # Try to use a bold system font, fall back to default
    font_size = max(sc(72), 10)  # minimum 10px to avoid division by zero
    font = None
    bold_fonts = [
        "/System/Library/Fonts/Helvetica.ttc",
        "/System/Library/Fonts/SFNSDisplay.ttf",
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/System/Library/Fonts/Supplemental/Helvetica.ttc",
    ]
    for fp in bold_fonts:
        try:
            font = ImageFont.truetype(fp, font_size)
            break
        except (IOError, OSError):
            continue
    if font is None:
        font = ImageFont.load_default()
    
    text = "30"
    # Get text bounding box
    bbox = draw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    tx = cx - tw // 2
    ty = cy - th // 2 + sc(8)  # slightly below center since hourglass neck is there
    
    # Draw text with slight shadow for depth
    shadow_offset = max(sc(2), 1)
    draw.text((tx + shadow_offset, ty + shadow_offset), text, fill=(0, 0, 0, 60), font=font)
    draw.text((tx, ty), text, fill=white, font=font)
    
    # --- Subtle clock tick marks around the edge ---
    tick_overlay = Image.new('RGBA', (s, s), (0, 0, 0, 0))
    tick_draw = ImageDraw.Draw(tick_overlay)
    
    tick_outer_r = sc(218)
    tick_inner_r = sc(206)
    tick_w = max(sc(2), 1)
    
    for i in range(12):
        angle = math.radians(i * 30 - 90)
        x1 = cx + int(tick_outer_r * math.cos(angle))
        y1 = cy + int(tick_outer_r * math.sin(angle))
        x2 = cx + int(tick_inner_r * math.cos(angle))
        y2 = cy + int(tick_inner_r * math.sin(angle))
        tick_draw.line([(x1, y1), (x2, y2)], fill=(255, 255, 255, 70), width=tick_w)
    
    tick_masked = Image.new('RGBA', (s, s), (0, 0, 0, 0))
    tick_masked.paste(tick_overlay, mask=mask)
    img = Image.alpha_composite(img, tick_masked)
    
    return img


def create_adaptive_foreground(size=1024):
    """Create Android adaptive icon foreground (108dp canvas, content in inner 72dp).
    The actual image is square; Android clips it with various masks.
    Adaptive icons use a 108x108 viewport where the visible area is the inner 72x72.
    So we need 18/108 = 16.67% padding on each side.
    """
    # We create at 1024 and let Android handle scaling
    # For the foreground, just the hourglass + 30 on transparent background
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    s = size
    cx, cy = s // 2, s // 2
    
    def sc(v):
        """Scale relative to 512, but shrink by ~67% to fit in safe zone."""
        return int(v * s / 512 * 0.62)
    
    white = (255, 255, 255, 255)
    white_glass = (255, 255, 255, 60)
    white_sand = (255, 255, 255, 140)
    white_stream = (255, 255, 255, 150)
    
    # Top plate
    plate_w = sc(210)
    plate_h = sc(12)
    plate_r = sc(4)
    draw_rounded_rect(draw,
        [cx - plate_w//2, cy - sc(180), cx + plate_w//2, cy - sc(180) + plate_h],
        plate_r, white)
    
    # Bottom plate
    draw_rounded_rect(draw,
        [cx - plate_w//2, cy + sc(168), cx + plate_w//2, cy + sc(168) + plate_h],
        plate_r, white)
    
    # Knobs
    knob_r = sc(8)
    draw.ellipse([cx - plate_w//2 - knob_r + sc(6), cy - sc(180) - knob_r + sc(5),
                  cx - plate_w//2 + knob_r + sc(6), cy - sc(180) + knob_r + sc(5)], fill=white)
    draw.ellipse([cx + plate_w//2 - knob_r - sc(6), cy - sc(180) - knob_r + sc(5),
                  cx + plate_w//2 + knob_r - sc(6), cy - sc(180) + knob_r + sc(5)], fill=white)
    draw.ellipse([cx - plate_w//2 - knob_r + sc(6), cy + sc(168) - knob_r + sc(7),
                  cx - plate_w//2 + knob_r + sc(6), cy + sc(168) + knob_r + sc(7)], fill=white)
    draw.ellipse([cx + plate_w//2 - knob_r - sc(6), cy + sc(168) - knob_r + sc(7),
                  cx + plate_w//2 + knob_r - sc(6), cy + sc(168) + knob_r + sc(7)], fill=white)
    
    # Glass body
    top_wide = sc(82)
    neck = sc(14)
    top_y = cy - sc(166)
    mid_y = cy - sc(4)
    bot_y = cy + sc(4)
    bottom_y = cy + sc(166)
    
    top_glass = [(cx - top_wide, top_y), (cx + top_wide, top_y),
                 (cx + neck, mid_y), (cx - neck, mid_y)]
    draw.polygon(top_glass, fill=white_glass, outline=white, width=max(sc(5), 1))
    
    bot_glass = [(cx - neck, bot_y), (cx + neck, bot_y),
                 (cx + top_wide, bottom_y), (cx - top_wide, bottom_y)]
    draw.polygon(bot_glass, fill=white_glass, outline=white, width=max(sc(5), 1))
    
    draw.ellipse([cx - neck - sc(2), cy - sc(12), cx + neck + sc(2), cy + sc(12)],
                 fill=white_glass, outline=white, width=max(sc(4), 1))
    
    # Sand top
    sand_top_w = sc(42)
    sand_top_y = cy - sc(100)
    sand_neck = sc(10)
    sand_mid_y = cy - sc(30)
    draw.polygon([(cx - sand_top_w, sand_top_y), (cx + sand_top_w, sand_top_y),
                  (cx + sand_neck, sand_mid_y), (cx - sand_neck, sand_mid_y)], fill=white_sand)
    draw.line([(cx - sand_top_w, sand_top_y), (cx + sand_top_w, sand_top_y)],
              fill=white_sand, width=max(sc(3), 1))
    
    # Sand stream
    stream_w = sc(4)
    draw.rectangle([cx - stream_w//2, cy - sc(28), cx + stream_w//2, cy + sc(28)],
                   fill=white_stream)
    
    # Sand pile bottom
    sand_pile_w = sc(58)
    sand_pile_neck = sc(12)
    sand_pile_mid = cy + sc(55)
    draw.polygon([(cx - sand_pile_neck, sand_pile_mid), (cx + sand_pile_neck, sand_pile_mid),
                  (cx + sand_pile_w, bottom_y), (cx - sand_pile_w, bottom_y)], fill=white_sand)
    
    # "30" text
    font_size = max(sc(72), 10)
    font = None
    for fp in ["/System/Library/Fonts/Helvetica.ttc",
               "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
               "/System/Library/Fonts/Supplemental/Helvetica.ttc"]:
        try:
            font = ImageFont.truetype(fp, font_size)
            break
        except (IOError, OSError):
            continue
    if font is None:
        font = ImageFont.load_default()
    
    text = "30"
    bbox = draw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    tx = cx - tw // 2
    ty = cy - th // 2 + sc(8)
    draw.text((tx + sc(2), ty + sc(2)), text, fill=(0, 0, 0, 60), font=font)
    draw.text((tx, ty), text, fill=white, font=font)
    
    # Tick marks
    tick_outer_r = sc(218)
    tick_inner_r = sc(206)
    tick_w = max(sc(2), 1)
    for i in range(12):
        angle = math.radians(i * 30 - 90)
        x1 = cx + int(tick_outer_r * math.cos(angle))
        y1 = cy + int(tick_outer_r * math.sin(angle))
        x2 = cx + int(tick_inner_r * math.cos(angle))
        y2 = cy + int(tick_inner_r * math.sin(angle))
        draw.line([(x1, y1), (x2, y2)], fill=(255, 255, 255, 180), width=tick_w)
    
    return img


def main():
    base_dir = "/Users/hcome/Projects/JW30s"
    icon_dir = os.path.join(base_dir, "icon")
    os.makedirs(icon_dir, exist_ok=True)
    
    # 1. Create full icon (with background) for iOS and preview
    print("Creating full icon at 1024x1024...")
    full_icon = create_icon(1024)
    
    # Save iOS icon (needs to be non-transparent for App Store)
    ios_icon = Image.new('RGB', (1024, 1024), (46, 125, 50))  # fallback bg
    ios_icon.paste(full_icon, mask=full_icon.split()[3])
    ios_path = os.path.join(base_dir, "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png")
    ios_icon.save(ios_path, "PNG")
    print(f"  -> iOS icon: {ios_path}")
    
    # Save preview
    full_icon.save(os.path.join(icon_dir, "preview_1024.png"), "PNG")
    print(f"  -> Preview: icon/preview_1024.png")
    
    # 2. Create Android adaptive icon foreground
    print("Creating Android adaptive foreground...")
    fg = create_adaptive_foreground(1024)
    
    # 3. Generate Android legacy mipmap PNGs (icon with background baked in)
    android_sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    
    res_dir = os.path.join(base_dir, "composeApp/src/androidMain/res")
    
    for folder, px in android_sizes.items():
        out_dir = os.path.join(res_dir, folder)
        os.makedirs(out_dir, exist_ok=True)
        
        # Legacy square icon
        legacy = create_icon(px)
        # Convert to RGB (no transparency for legacy)
        legacy_rgb = Image.new('RGB', (px, px), (46, 125, 50))
        legacy_rgb.paste(legacy, mask=legacy.split()[3])
        legacy_rgb.save(os.path.join(out_dir, "ic_launcher.png"), "PNG")
        
        # Legacy round icon - apply circular mask
        round_icon = create_icon(px)
        circle_mask = Image.new('L', (px, px), 0)
        circle_draw = ImageDraw.Draw(circle_mask)
        circle_draw.ellipse([0, 0, px, px], fill=255)
        round_rgb = Image.new('RGB', (px, px), (46, 125, 50))
        round_rgb.paste(round_icon, mask=circle_mask)
        round_rgb.save(os.path.join(out_dir, "ic_launcher_round.png"), "PNG")
        
        print(f"  -> {folder}: {px}x{px}")
    
    print("\nAll icons generated successfully!")


if __name__ == "__main__":
    main()
