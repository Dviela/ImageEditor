package com.svalero.ImageEditor.Filtros;

import java.awt.image.BufferedImage;

public class Sepia {

    public static BufferedImage aplicarFiltro(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);
                int nuevoPixel = getNuevoPixel(pixel);

                imagenSalida.setRGB(x, y, nuevoPixel);
            }
        }
        return imagenSalida;
    }

    private static int getNuevoPixel(int pixel) {
        int rojo = (pixel >> 16) & 0xFF;
        int verde = (pixel >> 8) & 0xFF;
        int azul = pixel & 0xFF;

        int nuevoRojo = (int) (0.393 * rojo + 0.769 * verde + 0.189 * azul);
        int nuevoVerde = (int) (0.349 * rojo + 0.686 * verde + 0.168 * azul);
        int nuevoAzul = (int) (0.272 * rojo + 0.534 * verde + 0.131 * azul);

        nuevoRojo = Math.min(nuevoRojo, 255);
        nuevoVerde = Math.min(nuevoVerde, 255);
        nuevoAzul = Math.min(nuevoAzul, 255);

        int nuevoPixel = (nuevoRojo << 16) | (nuevoVerde << 8) | nuevoAzul;
        return nuevoPixel;
    }
}
