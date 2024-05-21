package com.svalero.ImageEditor;

import com.svalero.ImageEditor.Filtros.AumentoBrillo;
import com.svalero.ImageEditor.Filtros.EscalaGrises;
import com.svalero.ImageEditor.Filtros.InvertirColor;
import com.svalero.ImageEditor.Filtros.Sepia;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
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

    private List<Image> loadedImages;

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
            for (File file : selectedFiles) {
                listaImagenes.getItems().add(file.getAbsolutePath());
            }
            if (!selectedFiles.isEmpty()) {
                // Load the first image to display initially
                Image firstImage = new Image(selectedFiles.get(0).toURI().toString());
                imageView.setImage(firstImage);
                listaImagenes.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void mostrarImagenSeleccionada() {
        String selectedFilePath = listaImagenes.getSelectionModel().getSelectedItem();
        if (selectedFilePath != null) {
            Image selectedImage = new Image(new File(selectedFilePath).toURI().toString());
            imageView.setImage(selectedImage);
        }
    }

    @FXML
    private void aplicarFiltro() {
        String selectedFilter = choiceFiltros.getValue();
        if (selectedFilter != null && imageView.getImage() != null) {
            Image filteredImage = null;
            switch (selectedFilter) {
                case "AumentoBrillo":
                    filteredImage = new AumentoBrillo().aplicar(imageView.getImage());
                    break;
                case "EscalaGrises":
                    filteredImage = new EscalaGrises().aplicar(imageView.getImage());
                    break;
                case "InvertirColor":
                    filteredImage = new InvertirColor().aplicar(imageView.getImage());
                    break;
                case "Sepia":
                    filteredImage = new Sepia().aplicar(imageView.getImage());
                    break;
            }
            if (filteredImage != null) {
                imageView.setImage(filteredImage);
            }
        }
    }


    @FXML
    private void guardarImagenes() {
        if (imageView.getImage() != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage((int) imageView.getImage().getWidth(), (int) imageView.getImage().getHeight());
                    imageView.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
