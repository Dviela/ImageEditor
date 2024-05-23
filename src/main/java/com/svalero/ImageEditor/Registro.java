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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy/HH:mm");
        return "NOMBRE: " + nombreImagen +
                ", RUTA: " + rutaImagen +
                ", FILTRO: " + filtroAplicado +
                ", FECHA/HORA: " + fechaHora.format(formatter);
    }


}
