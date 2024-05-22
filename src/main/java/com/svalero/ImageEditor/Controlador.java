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
import java.util.concurrent.ExecutionException;


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
    @FXML
    private TabPane tabPane;

    private List<Image> loadedImages;
    private List<ProgressBar> progressBars;
    private List<Label> progressLabels;

    @FXML
    public void initialize() {
        listaImagenes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> mostrarImagenSeleccionada());
        loadedImages = new ArrayList<>();
        progressBars = new ArrayList<>();
        progressLabels = new ArrayList<>();
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
            loadedImages.clear();
            for (File file : selectedFiles) {
                listaImagenes.getItems().add(file.getAbsolutePath());
                Image image = new Image(file.toURI().toString());
                loadedImages.add(image);
            }
        }
    }

    @FXML
    private void mostrarImagenSeleccionada() {
        int selectedIndex = listaImagenes.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //imageView.setImage(loadedImages.get(selectedIndex));
        }
    }

    private void filtroConProgreso(Image image, String filter, ProgressBar progressBar, Label progressLabel, ImageView imageView) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                for (int progress = 0; progress <= 100; progress++) {
                    updateProgress(progress, 100);
                    updateMessage(progress + "%");
                    Thread.sleep(50);
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

        progressBar.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            Image filteredImage = task.getValue();
            imageView.setImage(filteredImage); // Actualizar el ImageView con la imagen filtrada
        });
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }





    @FXML
    private void aplicarFiltro() {
        String selectedFilter = choiceFiltros.getValue();
        int selectedIndex = listaImagenes.getSelectionModel().getSelectedIndex();
        if (selectedFilter != null && loadedImages != null && !loadedImages.isEmpty() && selectedIndex >= 0) {
            Image image = loadedImages.get(selectedIndex);

            ProgressBar progressBar = new ProgressBar(0);
            Label progressLabel = new Label("0%");
            progressBars.add(progressBar);
            progressLabels.add(progressLabel);

            // Crear una nueva pestaña con la imagen original y la barra de progreso
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(400);  // Ajusta el tamaño máximo del ancho
            imageView.setFitHeight(300); // Ajusta el tamaño máximo del alto
            imageView.setPreserveRatio(true); // Mantiene la relación de aspecto
            VBox vbox = new VBox(imageView, progressBar, progressLabel);
            Tab tab = new Tab("Imagen " + (tabPane.getTabs().size() + 1), vbox);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            // Aplicar el filtro a la imagen
            filtroConProgreso(image, selectedFilter, progressBar, progressLabel, imageView);
        }
    }


    @FXML
    private void guardarImagenes() {
        if (!loadedImages.isEmpty()) {
            // Obtener la pestaña activa
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                ImageView imageView = (ImageView) ((VBox) selectedTab.getContent()).getChildren().get(0);
                Image originalImage = imageView.getImage();

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


    private void agregarPestanaConImagen(Image image) {
        ImageView imageView = new ImageView(image);
        Tab tab = new Tab("Imagen " + (tabPane.getTabs().size() + 1), imageView);
        tabPane.getTabs().add(tab);
        ProgressBar progressBar = new ProgressBar();
        Label progressLabel = new Label();
        VBox vBox = new VBox(progressBar, progressLabel);
        progressBox.getChildren().add(vBox);
        progressBars.add(progressBar);
        progressLabels.add(progressLabel);
    }
}
