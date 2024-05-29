package com.svalero.ImageEditor.filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class EscalaGrises {
    // Método para aplicar el filtro de escala de grises a una imagen
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

                // Calcula el valor de gris promedio de los componentes RGB del color
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                // Crea un nuevo color con el valor de gris calculado
                color = new Color(gray, gray, gray, color.getOpacity());

                // Escribe el nuevo color en la posición correspondiente en la imagen nueva
                pixelWriter.setColor(x, y, color);
            }
        }

        //Devuelve imagen nueva ya con filtro
        return writableImage;
    }
}
