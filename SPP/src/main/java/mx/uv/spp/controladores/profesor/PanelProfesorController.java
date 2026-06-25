/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.controladores.profesor;

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
 * Controlador del panel principal del Profesor Asesor
 * (panel_profesor.fxml). Gestiona la navegación entre las
 * sub-vistas del menú lateral y el cierre de sesión.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelProfesorController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private Label     lblBienvenida;

    private static final String VISTA_EVIDENCIAS =
            "/mx/uv/spp/vistas/profesor/evaluar_evidencia.fxml";
    private static final String VISTA_CALIFICACION_OV =
            "/mx/uv/spp/vistas/profesor/calificacion_ov.fxml";
    private static final String VISTA_PRORROGA =
            "/mx/uv/spp/vistas/profesor/otorgar_prorroga.fxml";
    private static final String VISTA_MENSAJE =
            "/mx/uv/spp/vistas/profesor/publicar_mensaje.fxml";

    /**
     * Inicializa el panel: muestra el nombre del Profesor
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
     * Carga la sub-vista de Evidencias por Evaluar (CU-29).
     */
    @FXML
    private void onBtnEvidencias() {
        cargarVista(VISTA_EVIDENCIAS);
    }

    /**
     * Carga la sub-vista de Calificación OV (CU-26).
     */
    @FXML
    private void onBtnCalificacionOV() {
        cargarVista(VISTA_CALIFICACION_OV);
    }

    /**
     * Carga la sub-vista de Otorgar Prórroga (CU-30).
     */
    @FXML
    private void onBtnProrroga() {
        cargarVista(VISTA_PRORROGA);
    }

    /**
     * Carga la sub-vista de Publicar Mensaje al Grupo (CU-31).
     */
    @FXML
    private void onBtnMensaje() {
        cargarVista(VISTA_MENSAJE);
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
                    "Error al cargar vista en panel profesor: "
                    + e.getMessage());
        }
    }

}
