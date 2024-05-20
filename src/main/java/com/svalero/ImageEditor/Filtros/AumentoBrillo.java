package com.svalero.ImageEditor.Filtros;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class AumentoBrillo {

    public static BufferedImage aplicarFiltro(BufferedImage imagen, int factor) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = imagen.getRaster();

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int[] pixel = raster.getPixel(x, y, (int[]) null);
                for (int i = 0; i < pixel.length; i++) {
                    pixel[i] = Math.min(pixel[i] + factor, 255); // Aumento del brillo
                }
                imagenSalida.setRGB(x, y, (pixel[0] << 16) | (pixel[1] << 8) | pixel[2]);
            }
        }
        return imagenSalida;
    }
}
