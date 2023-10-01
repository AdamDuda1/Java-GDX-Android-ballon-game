package com.adamgamesstudio.android.poptheballoon.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {
    // Calculate relative luminance for a given color component in the sRGB color space
    private static float calculateRelativeLuminance(float colorComponent) {
        float c = colorComponent / 255.0f;
        return (c <= 0.03928f) ? (c / 12.92f) : (float) Math.pow((c + 0.055f) / 1.055f, 2.4f);
    }

    // Calculate contrast ratio between two colors
    public static double calculateContrastRatio(Color foregroundColor, Color backgroundColor) {
        float L1 = 0.2126f * calculateRelativeLuminance(foregroundColor.r) +
                0.7152f * calculateRelativeLuminance(foregroundColor.g) +
                0.0722f * calculateRelativeLuminance(foregroundColor.b);

        float L2 = 0.2126f * calculateRelativeLuminance(backgroundColor.r) +
                0.7152f * calculateRelativeLuminance(backgroundColor.g) +
                0.0722f * calculateRelativeLuminance(backgroundColor.b);

        return (L1 + 0.05) / (L2 + 0.05);
    }

    // Get the best text color (black or white) based on the background color
    public static Color getContrastColor(Color backgroundColor) {
        Color whiteColor = Color.WHITE;
        Color blackColor = Color.BLACK;

        // Calculate contrast ratios for both black and white text colors
        double contrastWhite = calculateContrastRatio(whiteColor, backgroundColor);
        double contrastBlack = calculateContrastRatio(blackColor, backgroundColor);

        // Return the text color with higher contrast ratio
        return (contrastWhite >= contrastBlack) ? whiteColor : blackColor;
    }
}
