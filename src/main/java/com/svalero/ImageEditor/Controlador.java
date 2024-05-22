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
    private ComboBox<String> choiceFiltros;
    @FXML
    private Button cargarImagenes;
    @FXML
    private Button aplicarFiltro;
    @FXML
    private Button guardarImagenes;
    @FXML
    private TabPane tabPane;

    private List<Image> loadedImages = new ArrayList<>();
    private List<ProgressBar> progressBars = new ArrayList<>();
    private List<Label> progressLabels = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();

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
            tabPane.getTabs().clear();
            loadedImages.clear();
            progressBars.clear();
            progressLabels.clear();
            imagePaths.clear();

            for (File file : selectedFiles) {
                String filePath = file.getAbsolutePath();
                String fileName = file.getName();
                listaImagenes.getItems().add(fileName);
                Image image = new Image(file.toURI().toString());
                loadedImages.add(image);
                imagePaths.add(filePath);
            }
        }
    }

    @FXML
    private void mostrarImagenSeleccionada() {
        int selectedIndex = listaImagenes.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Image image = loadedImages.get(selectedIndex);
            String filePath = imagePaths.get(selectedIndex);

            agregarPestanaConImagen(image, filePath);
        }
    }

    private void agregarPestanaConImagen(Image image, String filePath) {
        String fileName = new File(filePath).getName();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        ProgressBar progressBar = new ProgressBar(0);
        Label progressLabel = new Label("0%");
        progressBars.add(progressBar);
        progressLabels.add(progressLabel);

        VBox vBox = new VBox(imageView, progressBar, progressLabel);
        Tab tab = new Tab(fileName, vBox);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    @FXML
    private void aplicarFiltro() {
        String selectedFilter = choiceFiltros.getValue();
        if (selectedFilter != null && !loadedImages.isEmpty()) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                int index = tabPane.getTabs().indexOf(selectedTab);
                VBox vBox = (VBox) selectedTab.getContent();
                ImageView imageView = (ImageView) vBox.getChildren().get(0);
                ProgressBar progressBar = progressBars.get(index);
                Label progressLabel = progressLabels.get(index);
                Image image = imageView.getImage();
                filtroConProgreso(image, selectedFilter, index, progressBar, progressLabel, imageView);
            }
        }
    }

    private void filtroConProgreso(Image image, String filter, int index, ProgressBar progressBar, Label progressLabel, ImageView imageView) {
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

        task.setOnSucceeded(e -> imageView.setImage(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }

    @FXML
    private void guardarImagenes() {
        if (!loadedImages.isEmpty()) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                VBox vBox = (VBox) selectedTab.getContent();
                ImageView imageView = (ImageView) vBox.getChildren().get(0);
                Image originalImage = imageView.getImage();

                String selectedFilter = choiceFiltros.getValue();
                String selectedFilePath = listaImagenes.getSelectionModel().getSelectedItem();

                if (selectedFilePath != null && selectedFilter != null) {
                    File originalFile = new File(imagePaths.get(listaImagenes.getSelectionModel().getSelectedIndex()));
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
}
