package com.adamgamesstudio.android.poptheballoon.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageColorApproximation {
    public static Color getApproximateColor(FileHandle imageFile) {
        try {
            // Load the image
            BufferedImage image = ImageIO.read(convertFileHandleToFile(imageFile));

            // Resize the image to a small size (e.g., 5x5)
            int width = 5;
            int height = 5;
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);

            // Calculate the average color
            int totalRed = 0;
            int totalGreen = 0;
            int totalBlue = 0;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color pixelColor = new Color(resizedImage.getRGB(x, y));
                    totalRed += pixelColor.r * 255; // Convert float to integer range (0-255)
                    totalGreen += pixelColor.g * 255;
                    totalBlue += pixelColor.b * 255;
                }
            }

            int numPixels = width * height;
            int avgRed = totalRed / numPixels;
            int avgGreen = totalGreen / numPixels;
            int avgBlue = totalBlue / numPixels;

            // Create and return the approximate color
            return new Color(avgRed / 255f, avgGreen / 255f, avgBlue / 255f, 1f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File convertFileHandleToFile(FileHandle fileHandle) throws IOException {
        InputStream inputStream = fileHandle.read(); // Get the InputStream from the FileHandle

        // Create a temporary File to write the data
        File tempFile = File.createTempFile("temp_", null);
        tempFile.deleteOnExit();

        // Write the data from the InputStream to the temporary File
        OutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    public static Color getColor(FileHandle imageFile) {
        return getApproximateColor(imageFile);
    }
}