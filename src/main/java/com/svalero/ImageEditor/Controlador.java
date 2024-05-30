package com.svalero.ImageEditor;

import com.svalero.ImageEditor.filtros.*;
import com.svalero.ImageEditor.util.Constantes;
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
import java.util.concurrent.Semaphore;


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
    private final Map<Image, List<String>> filtrosAplicados = new HashMap<>();

    private final Semaphore imageSemaphore = new Semaphore(Constantes.MAX_CONCURRENT_IMAGES);

    @FXML
    public void initialize() {
        listaImagenes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> mostrarImagenSeleccionada());
    }
//Métos para CARGAR IMAGENES que queremos filtrar
    @FXML
    private void cargarImagenes() {     //Carga Imágenes seleccionadas "a mano"
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
    private void cargarCarpeta() {      //Carga por lotes. Todas las imágenes de la carpeta seleccionada
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar Carpeta con Imágenes");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            limpiarContenido();
            cargarImagenesDesdeCarpeta(selectedDirectory);
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
    @FXML
    private void mostrarImagenSeleccionada() {      //Muestra la imagen que hemos seleccionado de todas las cargadas
        int selectedIndex = listaImagenes.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Image image = loadedImages.get(selectedIndex);
            String filePath = imagePaths.get(selectedIndex);

            agregarPestanaConImagen(image, filePath);
        }
    }

    private void agregarPestanaConImagen(Image image, String filePath) {    //Añade una pestaña al TabPane cuando seleccionamos una imagen cargada
        String fileName = new File(filePath).getName();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        VBox vBox = new VBox(imageView);
        Tab tab = new Tab(fileName, vBox);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

//Metodos para aplicación de los FILTROS a imagenes cargadas
    @FXML
    private void aplicarFiltro() {  //Botón Aplica filtros
        if (imageSemaphore.tryAcquire()) { // Adquirir permiso del "semáforo"
            List<CheckBox> selectedCheckBoxes = obtenerCheckBoxesSeleccionados();

            if (!loadedImages.isEmpty() && !selectedCheckBoxes.isEmpty()) {
                Tab selectedTab = obtenerPestanaSeleccionada();
                if (selectedTab != null) {
                    int index = obtenerIndicePestanaSeleccionada();
                    ImageView imageView = obtenerImageViewDesdePestana(selectedTab);

                    Image image = imageView.getImage();
                    imagenActualEnDeshacer(image);
                    limpiarPilaRehacer();

                    aplicarFiltrosConProgreso(image, selectedCheckBoxes, index, imageView);
                }
            }
        } else { // Si no se puede adquirir permiso del "semáforo"
            AlertManager.mostrarAlerta("Número máximo de imágenes en proceso superadas.");
        }
    }

    private void aplicarFiltrosConProgreso(Image initialImage, List<CheckBox> checkBoxes, int index, ImageView  //Coordina la aplicación de filtros a una imagen de manera asíncrona
            imageView) {
        VBox vBox = obtenerVBoxContenedor(imageView);

        Task<Image> task = crearTareaFiltro(initialImage, checkBoxes, imageView, vBox, index);

        task.setOnSucceeded(e -> {
            imageView.setImage(task.getValue());
            imageSemaphore.release(); // Al terminar liberar el permiso del semáforo
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            imageSemaphore.release(); // Si falla, libera el permiso
        });


        new Thread(task).start();
    }

    private List<CheckBox> obtenerCheckBoxesSeleccionados() {   //Obtiene que filtros se han seleccionado
        List<CheckBox> selectedCheckBoxes = new ArrayList<>();
        for (Node node : choiceFiltrosPane.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedCheckBoxes.add(checkBox);
            }
        }
        return selectedCheckBoxes;
    }

    private void agregarBarraDeProgreso(VBox vBox, ProgressBar progressBar, Label progressLabel) {  //Añade barra de progeso por cada filtro
        javafx.application.Platform.runLater(() -> {
            vBox.getChildren().addAll(progressBar, progressLabel);
        });
    }

    private Tab obtenerPestanaSeleccionada() {  //Recoge en que pestaña del TabPane estámos para aplicar filtro o guardar imagen
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private int obtenerIndicePestanaSeleccionada() {
        return tabPane.getTabs().indexOf(obtenerPestanaSeleccionada());
    }

    private ImageView obtenerImageViewDesdePestana(Tab tab) {   //Devuelve la imagen que queremos ver en su pestaña
        VBox vBox = (VBox) tab.getContent();
        return (ImageView) vBox.getChildren().get(0);
    }

    private void imagenActualEnDeshacer(Image image) {  //Gestiona la memoria para poder deshacer
        undoStack.push(image);
    }

    private void limpiarPilaRehacer() { //Gestiona la memoria para poder rehacer
        redoStack.clear();
    }

    private VBox obtenerVBoxContenedor(ImageView imageView) {   //Devuelve el Vbox que contiene la ImageView
        return (VBox) imageView.getParent();
    }

    private Task<Image> crearTareaFiltro(Image initialImage, List<CheckBox> checkBoxes, ImageView   //Gestiona los filtros seleccionados y el progreso de la tarea
            imageView, VBox vBox, int index) {
        return new Task<>() {
            @Override
            protected Image call() throws Exception {
                Image currentImage = initialImage;
                List<String> filtersApplied = new ArrayList<>();

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        String filter = checkBox.getText();
                        ProgressBar filterProgressBar = new ProgressBar(0);
                        Label filterProgressLabel = new Label("0%");
                        agregarBarraDeProgreso(vBox, filterProgressBar, filterProgressLabel);

                        for (int progress = 0; progress <= 100; progress++) {
                            updateProgress(progress / 100.0, 1);
                            updateMessage(progress + "%");
                            Thread.sleep(50);

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
                            case "Verde" -> new Verde().aplicar(currentImage);
                            case "Reducir Saturación" -> new ReducirSaturacion().aplicar(currentImage);
                            default -> null;
                        };
                        filtersApplied.add(filter);
                    }
                }

                String filePath = imagePaths.get(index);
                String fileName = new File(filePath).getName();
                String filtersString = String.join(", ", filtersApplied);
                Registro registro = new Registro(fileName, filePath, filtersString, LocalDateTime.now());
                historial.add(registro);
                guardarHistorialEnArchivo(registro);
                filtrosAplicados.put(currentImage, filtersApplied);

                return currentImage;
            }
        };
    }

// Métodos para GUARDAR las imágenes procesadas
    @FXML
    private void guardarImagenes() {
        // Verifica si hay imágenes cargadas
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
                } else {    // Si no se han seleccionado filtros
                    AlertManager.mostrarAlerta("Por favor, debe aplicar al menos un filtro antes de guardar la imagen.");
                }
            }
        }else {
            AlertManager.mostrarAlerta("No hay ninguna imagen cargada");
        }
    }


    private List<String> obtenerFiltrosAplicados(Image imagen) { // Ppara obtener los filtros aplicados a una imagen
        return filtrosAplicados.getOrDefault(imagen, new ArrayList<>());
    }


    private File mostrarVentanaGuardardo(Tab pestanaSeleccionada, List<String> filtrosAplicados) {  //Para mostrar la ventana de guardar archivo
        // Crea un selector de archivo y configurar nombre inicial
        FileChooser selectorArchivo = new FileChooser();
        selectorArchivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PNG (*.png)", "*.png"));

        String nombreArchivoOriginal = pestanaSeleccionada.getText();
        String nombreArchivoSinExtension = nombreArchivoOriginal.substring(0, nombreArchivoOriginal.lastIndexOf('.'));
        String nuevoNombreArchivo = String.join("_", filtrosAplicados) + "_" + nombreArchivoSinExtension + ".png";

        selectorArchivo.setInitialFileName(nuevoNombreArchivo);

        // Establece ruta de imagen original como predeterminada
        File directorioInicial = new File(imagePaths.get(listaImagenes.getSelectionModel().getSelectedIndex())).getParentFile();
        selectorArchivo.setInitialDirectory(directorioInicial);

        // Mostrar ventana de guardado y devuelve el archivo seleccionado
        return selectorArchivo.showSaveDialog(null);
    }


    private void guardarEnRuta(Image imagen, File ruta) { // Para guardar la imagen donde queramos
        try {
            BufferedImage imagenBuffered = SwingFXUtils.fromFXImage(imagen, null);
            ImageIO.write(imagenBuffered, "png", ruta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        System.exit(0);
    }

//Metodos para el HISTORIAL
    @FXML
    private void verHistorial() { //Para ver archivo historial.txt desde el botón de la aplicación
        File historialFile = new File(Constantes.HISTORY_PATH);
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

    private void guardarHistorialEnArchivo(Registro registro) {
        String rutaHistorial = Constantes.HISTORY_PATH;

        try (FileWriter fw = new FileWriter(rutaHistorial, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(registro.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//Métodos DESHACER/REHACER CAMBIOS
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

