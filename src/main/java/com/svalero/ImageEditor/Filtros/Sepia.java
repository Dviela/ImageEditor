package com.svalero.ImageEditor.Filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Sepia {
    public Image aplicar(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image.getPixelReader().getColor(x, y);

                double tr = 0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue();
                double tg = 0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue();
                double tb = 0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue();

                if (tr > 1.0) tr = 1.0;
                if (tg > 1.0) tg = 1.0;
                if (tb > 1.0) tb = 1.0;

                color = new Color(tr, tg, tb, color.getOpacity());
                pixelWriter.setColor(x, y, color);
            }
        }

        return writableImage;
    }
}









