# ImageEditor - Aplicación de Filtros de Imagen

Esta aplicación permite cargar imágenes desde el sistema de archivos, aplicar una variedad de filtros a estas imágenes y guardar las imágenes resultantes. También proporciona la funcionalidad para ver un historial de las operaciones realizadas y deshacer y rehacer cambios en las imágenes.

## Funcionalidades

### Cargar Imágenes
- Se pueden cargar imágenes de forma individual seleccionando archivos desde el sistema de archivos.
- También se puede cargar un lote de imágenes al seleccionar una carpeta que contenga imágenes.

### Aplicar Filtros
- Se pueden aplicar varios filtros a las imágenes cargadas.
- Los filtros disponibles incluyen:
  - Aumento de Brillo
  - Escala de Grises
  - Invertir Color
  - Sepia
  - Verde
  - Reducir Saturación
- Los filtros se aplican de forma asíncrona para evitar bloquear la interfaz de usuario.

### Guardar Imágenes
- Las imágenes resultantes después de aplicar los filtros se pueden guardar en el sistema de archivos.
- Se puede elegir el nombre y la ubicación del archivo de imagen guardado.

### Ver Historial
- Se puede acceder a un archivo de historial que registra las operaciones realizadas en las imágenes.
- El historial muestra la información sobre las imágenes procesadas, los filtros aplicados y la fecha y hora de la operación.

### Deshacer y Rehacer Cambios
- Se pueden deshacer y rehacer los cambios realizados en las imágenes.
- Esto permite al usuario revertir las operaciones aplicadas y restaurar las imágenes a estados anteriores.

## Instrucciones de Uso

1. **Cargar Imágenes**: Haga clic en el botón "Cargar Imágenes" para seleccionar archivos de imagen o una carpeta que contenga imágenes.
2. **Aplicar Filtros**: Seleccione una imagen cargada y elija los filtros que desea aplicar. Luego, haga clic en el botón "Aplicar Filtro".
3. **Guardar Imágenes**: Después de aplicar los filtros, puede guardar la imagen resultante haciendo clic en el botón "Guardar Imagen".
4. **Ver Historial**: Haga clic en el botón "Ver Historial" para ver las operaciones realizadas en las imágenes.
5. **Deshacer y Rehacer Cambios**: Utilice las opciones en el menú superior-Editar "Deshacer" y "Rehacer" para revertir y restaurar cambios en las imágenes.

## Requisitos del Sistema

- Java JDK 17 o superior
- JavaFX

## Configuración y Ejecución

1. Clonar este repositorio en su máquina local.
2. Importar el proyecto en su IDE de Java.
3. Ejecutar la clase principal `Main` para iniciar la aplicación.
