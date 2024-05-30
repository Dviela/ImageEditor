package com.svalero.ImageEditor.filtros;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Verde {
    public Image aplicar(Image imagenOriginal) {
        int ancho = (int) imagenOriginal.getWidth();
        int alto = (int) imagenOriginal.getHeight();
        WritableImage imagenProcesada = new WritableImage(ancho, alto);
        PixelReader pixelReader = imagenOriginal.getPixelReader();
        PixelWriter pixelWriter = imagenProcesada.getPixelWriter();

        final double redWeight = 0.393;
        final double greenWeight = 0.769;
        final double blueWeight = 0.189;

        final double redWeightVerdoso = 0.769;
        final double greenWeightVerdoso = 0.993;
        final double blueWeightVerdoso = 0.189;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                Color color = pixelReader.getColor(x, y);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();

                double rNew = Math.min(1, (r * redWeight + g * greenWeight + b * blueWeight));
                double gNew = Math.min(1, (r * redWeightVerdoso + g * greenWeightVerdoso + b * blueWeightVerdoso));
                double bNew = Math.min(1, (r * redWeight + g * greenWeight + b * blueWeight));

                pixelWriter.setColor(x, y, Color.color(rNew, gNew, bNew));
            }
        }

        return imagenProcesada;
    }
}

