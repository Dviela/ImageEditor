package com.svalero.ImageEditor;

import com.svalero.ImageEditor.Filtros.EscalaGrises;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controlador {
    @FXML
    private ListView<File> listaImagenes;
    @FXML
    private ImageView imageView;

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private void cargarImagenes(ActionEvent event) {
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            listaImagenes.getItems().addAll(files);
            mostrarImagen(files.get(0)); // Mostrar la primera imagen cargada
        }
    }

    @FXML
    private void aplicarFiltro(ActionEvent event) {
        List<File> archivos = listaImagenes.getItems();
        if (archivos != null) {
            for (File archivo : archivos) {
                try {
                    BufferedImage imagen = ImageIO.read(archivo);
                    Filtro filtro = new EscalaGrises();
                    executor.execute(new ProcesadorImagen(imagen, filtro, archivo));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void guardarImagenes(ActionEvent event) {
        File directorio = directoryChooser.showDialog(null);
        if (directorio != null) {
            // Implementa la lógica para guardar imágenes en el directorio seleccionado
            System.out.println("Guardar imágenes en: " + directorio.getAbsolutePath());
        }
    }

    private void mostrarImagen(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
