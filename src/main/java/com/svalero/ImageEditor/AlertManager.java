package com.svalero.ImageEditor;

import javafx.scene.control.Alert;

public class AlertManager {

    // Método para mostrar una alerta informativa
    static void mostrarAlerta(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
