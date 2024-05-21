package com.svalero.ImageEditor.Filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class InvertirColor {
    public Image aplicar(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image.getPixelReader().getColor(x, y);
                color = new Color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue(), color.getOpacity());
                pixelWriter.setColor(x, y, color);
            }
        }

        return writableImage;
    }
}
