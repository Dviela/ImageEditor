package com.svalero.ImageEditor.filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Sepia {
    // Método para aplicar el filtro Sepia a una imagen
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

                // Calcula los componentes RGB para el efecto de sepia
                double tr = 0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue();
                double tg = 0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue();
                double tb = 0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue();

                // Ajusta los valores para asegurarse de que estén en el rango [0, 1] para evitar valores no permitidos
                if (tr > 1.0) tr = 1.0;
                if (tg > 1.0) tg = 1.0;
                if (tb > 1.0) tb = 1.0;

                // Crea un nuevo color con los valores calculados y la opacidad original
                color = new Color(tr, tg, tb, color.getOpacity());

                // Escribe el nuevo color en la posición correspondiente en la imagen nueva
                pixelWriter.setColor(x, y, color);
            }
        }

        // Devuelve la imagen con el filtro de sepia aplicado
        return writableImage;
    }
}









