/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Centralizador de navegación entre pantallas JavaFX.
 * Mantiene una referencia al Stage principal y expone un método
 * por cada vista raíz del sistema; ningún controlador necesita
 * conocer rutas FXML ni el Stage directamente.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class Navegador {

    private static final String FXML_LOGIN =
            "/mx/uv/spp/vistas/login.fxml";
    private static final String FXML_PANEL_ESTUDIANTE =
            "/mx/uv/spp/vistas/estudiante/panel_estudiante.fxml";
    private static final String FXML_PANEL_PROFESOR =
            "/mx/uv/spp/vistas/profesor/panel_profesor.fxml";
    private static final String FXML_PANEL_COORDINADOR =
            "/mx/uv/spp/vistas/coordinador/panel_coordinador.fxml";
    private static final String FXML_PANEL_ADMINISTRADOR =
            "/mx/uv/spp/vistas/administrador/PanelAdministrador.fxml";

    private static final int ANCHO_PANEL = 1100;
    private static final int ALTO_PANEL = 700;

    private static Stage escenario;

    private Navegador() {
    }

    /**
     * Registra el Stage principal de la aplicación.
     * Debe llamarse desde {@code App.start()} antes de mostrar
     * cualquier vista.
     *
     * @param stage Stage primario provisto por JavaFX; no nulo.
     * @throws IllegalArgumentException si {@code stage} es nulo.
     */
    public static void inicializar(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException(
                    "El Stage no puede ser nulo.");
        }
        escenario = stage;
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    public static void irALogin() {
        cargarVista(FXML_LOGIN, "SPP — Inicio de sesión");
    }

    /**
     * Navega al panel principal del Estudiante.
     */
    public static void irAPanelEstudiante() {
        cargarVista(FXML_PANEL_ESTUDIANTE,
                "SPP — Panel del Estudiante");
    }

    /**
     * Navega al panel principal del Profesor Asesor.
     */
    public static void irAPanelProfesor() {
        cargarVista(FXML_PANEL_PROFESOR,
                "SPP — Panel del Profesor");
    }

    /**
     * Navega al panel principal del Coordinador de P.P.
     */
    public static void irAPanelCoordinador() {
        cargarVista(FXML_PANEL_COORDINADOR,
                "SPP — Panel del Coordinador");
    }

    /**
     * Navega al panel principal del Administrador.
     */
    public static void irAPanelAdministrador() {
        cargarVista(FXML_PANEL_ADMINISTRADOR,
                "SPP — Panel del Administrador");
    }

    /**
     * Navega a la vista indicada por su ruta FXML en el classpath.
     * Permite navegar a cualquier pantalla sin necesitar un método
     * dedicado por cada vista.
     *
     * @param rutaFxml Ruta absoluta del FXML en el classpath; no nula.
     */
    public static void cambiarVista(String rutaFxml) {
        cargarVista(rutaFxml, "SPP");
    }

    /**
     * Carga el FXML indicado y lo muestra en el Stage principal.
     * Si el Stage no fue inicializado o el FXML no existe, muestra
     * un Alert de error sin interrumpir la ejecución.
     *
     * @param rutaFxml Ruta absoluta del FXML en el classpath.
     * @param titulo   Título a mostrar en la barra de la ventana.
     */
    private static void cargarVista(String rutaFxml,
            String titulo) {
        if (escenario == null) {
            mostrarAlertaError(
                    "Navegador no inicializado. Llame a "
                    + "Navegador.inicializar(stage) primero.");
            return;
        }
        try {
            FXMLLoader cargador = new FXMLLoader(
                    Navegador.class.getResource(rutaFxml));
            if (cargador.getLocation() == null) {
                throw new IOException(
                        "FXML no encontrado: " + rutaFxml);
            }
            Parent raiz = cargador.load();
            Scene escena = new Scene(raiz, ANCHO_PANEL,
                    ALTO_PANEL);
            escenario.setScene(escena);
            escenario.setTitle(titulo);
            escenario.setResizable(false);
        } catch (IOException e) {
            mostrarAlertaError(
                    "No se pudo cargar la vista:\n"
                    + e.getMessage());
            System.err.println(
                    "IOException en Navegador.cargarVista: "
                    + e.getMessage());
        }
    }

    /**
     * Muestra un Alert de tipo ERROR con el mensaje indicado.
     *
     * @param mensaje Texto descriptivo del error ocurrido.
     */
    private static void mostrarAlertaError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de navegación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
