package com.svalero.ImageEditor.Filtros;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class InvertirColor {

    public static BufferedImage aplicarFiltro(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = imagen.getRaster();

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int[] pixel = raster.getPixel(x, y, (int[]) null);
                pixel[0] = 255 - pixel[0]; // Rojo
                pixel[1] = 255 - pixel[1]; // Verde
                pixel[2] = 255 - pixel[2]; // Azul
                imagenSalida.setRGB(x, y, (pixel[0] << 16) | (pixel[1] << 8) | pixel[2]);
            }
        }
        return imagenSalida;
    }
}
