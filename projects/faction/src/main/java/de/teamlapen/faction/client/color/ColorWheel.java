package de.teamlapen.faction.client.color;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class ColorWheel {

    private ColorWheel() {
    }

    public static int rotate(int argb, float degrees) {
        int red = ARGB.red(argb);
        int green = ARGB.green(argb);
        int blue = ARGB.blue(argb);

        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        if (max == min) return argb;

        float hue = toRgbHue(wrap(toRybHue(toHue(red, green, blue, max, min)) - degrees));

        return Mth.hsvToArgb(hue / 360f, (float) (max - min) / max, max / 255f, ARGB.alpha(argb));
    }

    private static float toHue(int red, int green, int blue, int max, int min) {
        float range = max - min;

        float hue;
        if (max == red) {
            hue = (green - blue) / range;
        } else if (max == green) {
            hue = 2 + (blue - red) / range;
        } else {
            hue = 4 + (red - green) / range;
        }

        return wrap(hue * 60);
    }

    private static float toRybHue(float rgbHue) {
        if (rgbHue < 60) return rgbHue * 2;
        if (rgbHue < 120) return rgbHue + 60;
        if (rgbHue < 240) return rgbHue / 2 + 120;

        return rgbHue;
    }

    private static float toRgbHue(float rybHue) {
        if (rybHue < 120) return rybHue / 2;
        if (rybHue < 180) return rybHue - 60;
        if (rybHue < 240) return (rybHue - 120) * 2;

        return rybHue;
    }

    private static float wrap(float degrees) {
        float wrapped = degrees % 360;
        return wrapped < 0 ? wrapped + 360 : wrapped;
    }
}
