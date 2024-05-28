package com.svalero.ImageEditor;

import com.svalero.ImageEditor.filtros.AumentoBrillo;
import com.svalero.ImageEditor.filtros.EscalaGrises;
import com.svalero.ImageEditor.filtros.InvertirColor;
import com.svalero.ImageEditor.filtros.Sepia;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class Controlador {

    public VBox choiceFiltrosPane;
    @FXML
    private ListView<String> listaImagenes;
    @FXML
    private ComboBox<String> choiceFiltros;
    @FXML
    private Button cargarImagenes;
    @FXML
    private Button cargarCarpeta;
    @FXML
    private Button aplicarFiltro;
    @FXML
    private Button guardarImagenes;
    @FXML
    private TabPane tabPane;

    private final List<Image> loadedImages = new ArrayList<>();
    private final List<ProgressBar> progressBars = new ArrayList<>();
    private final List<Label> progressLabels = new ArrayList<>();
    private final List<String> imagePaths = new ArrayList<>();
    private final List<Registro> historial = new ArrayList<>();
    private final Stack<Image> undoStack = new Stack<>();
    private final Stack<Image> redoStack = new Stack<>();
    private Map<Image, List<String>> filtrosAplicados = new HashMap<>();

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
            limpiarContenido();
            cargarArchivosSeleccionados(selectedFiles);
        }
    }

    @FXML
    private void cargarCarpeta() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar Carpeta de Imágenes");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            limpiarContenido();
            cargarImagenesDesdeCarpeta(selectedDirectory);
        }
    }

    private void limpiarContenido() {
        listaImagenes.getItems().clear();
        tabPane.getTabs().clear();
        loadedImages.clear();
        progressBars.clear();
        progressLabels.clear();
        imagePaths.clear();
    }

    private void cargarArchivosSeleccionados(List<File> files) {
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            listaImagenes.getItems().add(fileName);
            Image image = new Image(file.toURI().toString());
            loadedImages.add(image);
            imagePaths.add(filePath);
        }
    }

    private void cargarImagenesDesdeCarpeta(File directory) {
        File[] files = directory.listFiles((dir, name) -> {
            String lowerCaseName = name.toLowerCase();
            return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".gif");
        });

        if (files != null) {
            cargarArchivosSeleccionados(Arrays.asList(files));
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

        VBox vBox = new VBox(imageView);
        Tab tab = new Tab(fileName, vBox);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void agregarBarraDeProgreso(VBox vBox, ProgressBar progressBar, Label progressLabel) {
        javafx.application.Platform.runLater(() -> {
            vBox.getChildren().addAll(progressBar, progressLabel);
        });
    }


    @FXML
    private void aplicarFiltro() {
        List<CheckBox> selectedCheckBoxes = new ArrayList<>();
        for (Node node : choiceFiltrosPane.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                if (checkBox.isSelected()) {
                    selectedCheckBoxes.add(checkBox);
                }
            }
        }

        if (!loadedImages.isEmpty() && !selectedCheckBoxes.isEmpty()) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                int index = tabPane.getTabs().indexOf(selectedTab);
                VBox vBox = (VBox) selectedTab.getContent();
                ImageView imageView = (ImageView) vBox.getChildren().get(0);

                Image image = imageView.getImage();
                undoStack.push(image); // Guardar la imagen actual en la pila de deshacer
                redoStack.clear(); // Limpiar la pila de rehacer

                // Llamar al método filtroConProgresoSecuencial con la lista de filtros seleccionados
                filtroConProgresoSecuencial(image, selectedCheckBoxes, index, imageView);
            }
        }
    }

    private void filtroConProgresoSecuencial(Image initialImage, List<CheckBox> checkBoxes, int index, ImageView imageView) {
        VBox vBox = (VBox) imageView.getParent(); // Obtener el VBox contenedor

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                Image currentImage = initialImage;
                List<String> filtersApplied = new ArrayList<>();

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        String filter = checkBox.getText();

                        // Crear una nueva barra de progreso
                        ProgressBar filterProgressBar = new ProgressBar(0);
                        Label filterProgressLabel = new Label("0%");

                        // Añadir la barra de progreso usando el método agregarBarraDeProgreso
                        agregarBarraDeProgreso(vBox, filterProgressBar, filterProgressLabel);

                        for (int progress = 0; progress <= 100; progress++) {
                            updateProgress(progress / 100.0, 1);
                            updateMessage(progress + "%");
                            Thread.sleep(50);

                            // Actualizar la barra de progreso específica del filtro
                            int finalProgress = progress;
                            javafx.application.Platform.runLater(() -> {
                                filterProgressBar.setProgress(finalProgress / 100.0);
                                filterProgressLabel.setText(finalProgress + "%");
                            });
                        }

                        currentImage = switch (filter) {
                            case "Aumento de Brillo" -> new AumentoBrillo().aplicar(currentImage);
                            case "Escala de Grises" -> new EscalaGrises().aplicar(currentImage);
                            case "Invertir Color" -> new InvertirColor().aplicar(currentImage);
                            case "Sepia" -> new Sepia().aplicar(currentImage);
                            default -> null;
                        }; // Actualizar la imagen para el siguiente filtro

                        filtersApplied.add(filter);
                    }
                }

                // Agregar registro al historial
                String filePath = imagePaths.get(index);
                String fileName = new File(filePath).getName();
                String filtersString = String.join(", ", filtersApplied);
                Registro registro = new Registro(fileName, filePath, filtersString, LocalDateTime.now());
                historial.add(registro);
                guardarHistorialEnArchivo(registro);

                // Actualizar filtrosAplicados
                filtrosAplicados.put(currentImage, filtersApplied);

                return currentImage;
            }
        };

        task.setOnSucceeded(e -> imageView.setImage(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }


    private void guardarHistorialEnArchivo(Registro registro) {
        // Para poder cambiar la ruta donde guardar el archivo historial.txt donde quiera
        String rutaHistorial = "logs/historial.txt";

        try (FileWriter fw = new FileWriter(rutaHistorial, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(registro.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
// Métodos para guardar las imágenes
    private void guardarImagenes() {
        // Verificar si hay imágenes cargadas
        if (!loadedImages.isEmpty()) {
            // Obtener la pestaña seleccionada en el TabPane
            Tab pestanaSeleccionada = tabPane.getSelectionModel().getSelectedItem();
            if (pestanaSeleccionada != null) {
                // Obtener el contenedor VBox y la imagen actual de la pestaña
                VBox contenedorVBox = (VBox) pestanaSeleccionada.getContent();
                ImageView imagenActual = (ImageView) contenedorVBox.getChildren().get(0);
                Image imagen = imagenActual.getImage();

                // Obtener los filtros aplicados a esta imagen
                List<String> filtrosAplicados = obtenerFiltrosAplicados(imagen);

                // Verificar si se han aplicado filtros a la imagen
                if (!filtrosAplicados.isEmpty()) {
                    // Mostrar ventana para guardar el archivo
                    File archivoSeleccionado = mostrarVentanaGuardardo(pestanaSeleccionada, filtrosAplicados);
                    if (archivoSeleccionado != null) {
                        // Guardar la imagen en el archivo seleccionado
                        guardarEnRuta(imagen, archivoSeleccionado);
                    }
                } else {
                    // Mostrar una alerta de error si no se han seleccionado filtros
                    mostrarAlerta("Por favor, seleccione al menos un filtro antes de guardar la imagen.");
                }
            }
        }
    }

    // Método para obtener los filtros aplicados a una imagen
    private List<String> obtenerFiltrosAplicados(Image imagen) {
        return filtrosAplicados.getOrDefault(imagen, new ArrayList<>());
    }

    // Método para mostrar el diálogo de guardar archivo
    private File mostrarVentanaGuardardo(Tab pestanaSeleccionada, List<String> filtrosAplicados) {
        // Crear un selector de archivo y configurar filtros y nombre inicial
        FileChooser selectorArchivo = new FileChooser();
        selectorArchivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PNG (*.png)", "*.png"));

        String nombreArchivoOriginal = pestanaSeleccionada.getText();
        String nombreArchivoSinExtension = nombreArchivoOriginal.substring(0, nombreArchivoOriginal.lastIndexOf('.'));
        String nuevoNombreArchivo = String.join("_", filtrosAplicados) + "_" + nombreArchivoSinExtension + ".png";

        selectorArchivo.setInitialFileName(nuevoNombreArchivo);

        // Establecer la ruta de la imagen original como predeterminada
        File directorioInicial = new File(imagePaths.get(listaImagenes.getSelectionModel().getSelectedIndex())).getParentFile();
        selectorArchivo.setInitialDirectory(directorioInicial);

        // Mostrar el diálogo de guardar y devolver el archivo seleccionado
        return selectorArchivo.showSaveDialog(null);
    }

    // Método para guardar la imagen donde deseemos
    private void guardarEnRuta(Image imagen, File ruta) {
        // Convertir la imagen a BufferedImage y guardarla en el archivo
        try {
            BufferedImage imagenBuffered = SwingFXUtils.fromFXImage(imagen, null);
            ImageIO.write(imagenBuffered, "png", ruta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Método para mostrar una alerta de error
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    //Método para ver archivo historial.txt desde la aplicación
    @FXML
    private void verHistorial() {
        File historialFile = new File("logs/historial.txt");
        if (historialFile.exists()) {
            try {
                Desktop.getDesktop().open(historialFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No se ha encontrado ningún historial.");
        }
    }

    @FXML
    private void salir() {
        System.exit(0);
    }


    @FXML
    private void deshacerCambios() {
        gestorDeCambios(undoStack, redoStack);
    }

    private void gestorDeCambios(Stack<Image> undoStack, Stack<Image> redoStack) {
        // Verifica si la pila de deshacer no está vacía
        if (!undoStack.isEmpty()) {
            // Obtiene la pestaña seleccionada
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                // Obtiene el contenedor de la imagen y la imagen actual
                VBox vBox = (VBox) selectedTab.getContent();
                ImageView imageView = (ImageView) vBox.getChildren().get(0);

                // Guarda la imagen actual en la pila de rehacer
                Image currentImage = imageView.getImage();
                redoStack.push(currentImage);

                // Recupera la imagen anterior de la pila de deshacer
                Image previousImage = undoStack.pop();

                // Establece la imagen anterior en el ImageView
                imageView.setImage(previousImage);
            }
        }
    }

    @FXML
    private void rehacerCambios() {
        gestorDeCambios(redoStack, undoStack);
    }

}

