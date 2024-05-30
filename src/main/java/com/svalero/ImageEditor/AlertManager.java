package com.svalero.ImageEditor;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AlertManager {

    // Método para mostrar una alerta informativa
    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");

        // Configurar el contenido de la ventana de alerta
        Label etiqueta = new Label("Atención:");
        TextArea textoArea = new TextArea(mensaje);
        textoArea.setEditable(false);
        textoArea.setWrapText(true);

        // Ajustar el tamaño del textoArea
        textoArea.setMaxWidth(Double.MAX_VALUE);
        textoArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textoArea, Priority.ALWAYS);
        GridPane.setHgrow(textoArea, Priority.ALWAYS);

        // Crear un nuevo GridPane para el contenido
        GridPane contenido = new GridPane();
        contenido.setMaxWidth(Double.MAX_VALUE);
        contenido.add(etiqueta, 0, 0);
        contenido.add(textoArea, 0, 1);

        // Establecer el contenido personalizado en la ventana de alerta
        alerta.getDialogPane().setContent(contenido);

        // Mostrar la ventana de alerta y esperar hasta que el usuario la cierre
        alerta.showAndWait();
    }
}