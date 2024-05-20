package com.svalero.ImageEditor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProcesadorImagen implements Runnable {
    private BufferedImage imagen;
    private Filtro filtro;
    private File archivoOriginal;

    public ProcesadorImagen(BufferedImage imagen, Filtro filtro, File archivoOriginal) {
        this.imagen = imagen;
        this.filtro = filtro;
        this.archivoOriginal = archivoOriginal;
    }

    @Override
    public void run() {
        BufferedImage imagenProcesada = filtro.aplicar(imagen);
        File archivoNuevo = new File(archivoOriginal.getParent(), "procesada_" + archivoOriginal.getName());
        try {
            ImageIO.write(imagenProcesada, "jpg", archivoNuevo);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
