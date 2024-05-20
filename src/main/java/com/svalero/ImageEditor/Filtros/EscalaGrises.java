package com.svalero.ImageEditor.Filtros;

import com.svalero.ImageEditor.Filtro;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class EscalaGrises extends Filtro {
    @Override
    public BufferedImage aplicar(BufferedImage imagen) {
        BufferedImage imagenGris = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = imagenGris.getGraphics();
        g.drawImage(imagen, 0, 0, null);
        g.dispose();
        return imagenGris;
    }
}
