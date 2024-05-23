package com.svalero.ImageEditor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Registro {
    private String nombreImagen;
    private String rutaImagen;
    private String filtroAplicado;
    private LocalDateTime fechaHora;

    public Registro(String nombreImagen, String rutaImagen, String filtroAplicado, LocalDateTime fechaHora) {
        this.nombreImagen = nombreImagen;
        this.rutaImagen = rutaImagen;
        this.filtroAplicado = filtroAplicado;
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Nombre de la imagen: " + nombreImagen +
                ", Ruta de la imagen: " + rutaImagen +
                ", Filtro aplicado: " + filtroAplicado +
                ", Fecha y hora: " + fechaHora.format(formatter);
    }


}
