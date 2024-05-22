package com.svalero.ImageEditor;

import com.svalero.ImageEditor.Filtros.AumentoBrillo;
import com.svalero.ImageEditor.Filtros.EscalaGrises;
import com.svalero.ImageEditor.Filtros.InvertirColor;
import com.svalero.ImageEditor.Filtros.Sepia;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Controlador {

    @FXML
    private ListView<String> listaImagenes;
    @FXML
    private ImageView imageView;
    @FXML
    private ComboBox<String> choiceFiltros;
    @FXML
    private Button cargarImagenes;
    @FXML
    private Button aplicarFiltro;
    @FXML
    private Button guardarImagenes;
    @FXML
    private VBox progressBox;

    private List<Image> loadedImages;
    private List<ProgressBar> progressBars;
    private List<Label> progressLabels;

    @FXML
    public void initialize() {
        listaImagenes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> mostrarImagenSeleccionada());
    }

    @FXML
    private void cargarImagenes() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            listaImagenes.getItems().clear();
            progressBox.getChildren().clear();
            loadedImages = new ArrayList<>();
            for (File file : selectedFiles) {
                listaImagenes.getItems().add(file.getAbsolutePath());
                loadedImages.add(new Image(file.toURI().toString()));
            }
            if (!selectedFiles.isEmpty()) {
                imageView.setImage(loadedImages.get(0));
                listaImagenes.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void mostrarImagenSeleccionada() {
        int selectedIndex = listaImagenes.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            imageView.setImage(loadedImages.get(selectedIndex));
        }
    }

    private void filtroConProgreso(Image image, String filter, int index, ProgressBar progressBar, Label progressLabel) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                for (int progress = 0; progress <= 100; progress++) {
                    // Calcular el progreso en pasos de 1/100
                    updateProgress(progress, 100);

                    // Actualizar el texto de la etiqueta de progreso
                    updateMessage(progress + "%");

                    // Simular retardo
                    Thread.sleep(50);  // Ajusta este valor según sea necesario
                }

                Image filteredImage = null;
                switch (filter) {
                    case "AumentoBrillo":
                        filteredImage = new AumentoBrillo().aplicar(image);
                        break;
                    case "EscalaGrises":
                        filteredImage = new EscalaGrises().aplicar(image);
                        break;
                    case "InvertirColor":
                        filteredImage = new InvertirColor().aplicar(image);
                        break;
                    case "Sepia":
                        filteredImage = new Sepia().aplicar(image);
                        break;
                }

                return filteredImage;
            }
        };

        // Vincula la barra de progreso con el progreso del task
        progressBar.progressProperty().bind(task.progressProperty());

        // Vincular el porcentaje con task
        progressLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> loadedImages.set(index, task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }




    @FXML
    private void aplicarFiltro() {
        String selectedFilter = choiceFiltros.getValue();
        if (selectedFilter != null && loadedImages != null && !loadedImages.isEmpty()) {
            progressBox.getChildren().clear();
            for (int i = 0; i < loadedImages.size(); i++) {
                ProgressBar progressBar = new ProgressBar(0);
                Label progressLabel = new Label("0%");
                progressBox.getChildren().addAll(progressBar, progressLabel);

                Image image = loadedImages.get(i);
                filtroConProgreso(image, selectedFilter, i, progressBar, progressLabel);
            }
        }
    }


    @FXML
    private void guardarImagenes() {
        if (imageView.getImage() != null) {
            // Obtener el filtro seleccionado
            String selectedFilter = choiceFiltros.getValue();
            // Obtener la ruta del archivo seleccionado de la lista
            String selectedFilePath = listaImagenes.getSelectionModel().getSelectedItem();

            if (selectedFilePath != null && selectedFilter != null) {
                File originalFile = new File(selectedFilePath);
                String originalFileName = originalFile.getName();
                String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                String newFileName = selectedFilter + "_" + fileNameWithoutExtension + ".png";

                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
                fileChooser.setInitialFileName(newFileName);
                File file = fileChooser.showSaveDialog(null);

                if (file != null) {
                    try {
                        // Guardar la imagen en su tamaño original
                        Image originalImage = imageView.getImage();
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


}
