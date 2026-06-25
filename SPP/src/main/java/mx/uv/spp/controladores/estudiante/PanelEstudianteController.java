/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.controladores.estudiante;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador del panel principal del Estudiante
 * (panel_estudiante.fxml). Gestiona la navegación entre las
 * sub-vistas del menú lateral y el cierre de sesión.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelEstudianteController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private Label     lblBienvenida;

    private static final String VISTA_ELEGIR =
            "/mx/uv/spp/vistas/estudiante/elegir_proyecto.fxml";
    private static final String VISTA_DOCS =
            "/mx/uv/spp/vistas/estudiante/documentos_iniciales.fxml";
    private static final String VISTA_REPORTES =
            "/mx/uv/spp/vistas/estudiante/reportes_informes.fxml";
    private static final String VISTA_AUTO =
            "/mx/uv/spp/vistas/estudiante/autoevaluacion.fxml";

    /**
     * Inicializa el panel: muestra el nombre del Estudiante
     * autenticado en la etiqueta de bienvenida.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        String nombre = SesionUsuario.getNombreCompleto();
        if (lblBienvenida != null && !nombre.isEmpty()) {
            lblBienvenida.setText("Bienvenido, " + nombre);
        }
    }

    /* ── Manejadores de botones del menú ────────────────────── */

    /**
     * Carga la sub-vista de Elegir Proyecto (CU-21).
     */
    @FXML
    private void onBtnElegirProyecto() {
        cargarVista(VISTA_ELEGIR);
    }

    /**
     * Carga la sub-vista de Documentos Iniciales.
     */
    @FXML
    private void onBtnDocumentosIniciales() {
        cargarVista(VISTA_DOCS);
    }

    /**
     * Carga la sub-vista de Reportes e Informes.
     */
    @FXML
    private void onBtnReportesInformes() {
        cargarVista(VISTA_REPORTES);
    }

    /**
     * Carga la sub-vista de Autoevaluación (CU-30).
     */
    @FXML
    private void onBtnAutoevaluacion() {
        cargarVista(VISTA_AUTO);
    }

    /**
     * Limpia la sesión activa y vuelve a la pantalla de login.
     */
    @FXML
    private void onBtnCerrarSesion() {
        SesionUsuario.limpiar();
        Navegador.irALogin();
    }

    /* ── Carga de sub-vistas ────────────────────────────────── */

    /**
     * Carga el FXML indicado como contenido central del panel.
     * Reemplaza la vista previa en el contenedor.
     *
     * @param rutaFxml Ruta del FXML en el classpath; no nula.
     */
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    getClass().getResource(rutaFxml));
            if (cargador.getLocation() == null) {
                throw new IOException(
                        "FXML no encontrado: " + rutaFxml);
            }
            Parent raiz = cargador.load();
            contenedor.getChildren().setAll(raiz);
        } catch (IOException e) {
            System.err.println(
                    "Error al cargar vista en panel: "
                    + e.getMessage());
        }
    }

}
