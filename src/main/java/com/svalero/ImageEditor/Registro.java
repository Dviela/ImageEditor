package com.svalero.ImageEditor;

import java.time.LocalDateTime;

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

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getFiltroAplicado() {
        return filtroAplicado;
    }

    public void setFiltroAplicado(String filtroAplicado) {
        this.filtroAplicado = filtroAplicado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        return "Nombre de la Imagen: " + nombreImagen + ", Ruta de la Imagen: " + rutaImagen +
                ", Filtro Aplicado: " + filtroAplicado + ", Fecha y Hora: " + fechaHora;
    }
}
