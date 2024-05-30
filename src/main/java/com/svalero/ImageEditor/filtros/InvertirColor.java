package com.svalero.ImageEditor.filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class InvertirColor {
    // Método para aplicar el filtro de inversión de color a una imagen
    public Image aplicar(Image image) {
        //Recoge medidas de la imagen
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        //Crea imagen para aplicar filtro
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        //Recorre los pixels de la imagen original
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Obtiene sus colores
                Color color = image.getPixelReader().getColor(x, y);

                // Invierte los componentes RGB del color original
                color = new Color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue(), color.getOpacity());

                // Escribe el nuevo color en la posición correspondiente en la imagen nueva
                pixelWriter.setColor(x, y, color);
            }
        }

        //Devuelve imagen nueva ya con filtro
        return writableImage;
    }
}
