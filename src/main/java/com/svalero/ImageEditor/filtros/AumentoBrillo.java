package com.svalero.ImageEditor.filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AumentoBrillo {
    public Image aplicar(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Factor de aumento de brillo
        final double factor = 0.2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Obtener el color del píxel actual
                Color color = pixelReader.getColor(x, y);

                // Calcular nuevos componentes de color con aumento de brillo
                double r = Math.min(1.0, color.getRed() + factor);
                double g = Math.min(1.0, color.getGreen() + factor);
                double b = Math.min(1.0, color.getBlue() + factor);

                // Asignar el nuevo color al píxel en la imagen resultante
                pixelWriter.setColor(x, y, Color.color(r, g, b));
            }
        }
        return writableImage;
    }
}
